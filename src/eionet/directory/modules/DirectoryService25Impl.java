/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "UIT".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (c) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.directory.modules;

import eionet.directory.DirectoryServiceIF;
import eionet.directory.DirServiceException;
import eionet.directory.FileServiceIF;

import javax.naming.NameClassPair;
import java.util.Vector;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingException;
import javax.naming.NameNotFoundException;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.NamingEnumeration;
import javax.naming.CommunicationException;
import javax.naming.AuthenticationException;

/**
 * Provides functionality related to LDAP directory server as login,
 * getting roles and getting e-mail addresses for users of a certain role.
 *
 * @author  Kaido Laine
 * @version 1.0
 */
public class DirectoryService25Impl implements DirectoryServiceIF {

	/** */
	private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	/** */
	private String ldapUrl;
	private String ldapCtx;
	private String ldapBackUpUrl;
	
	private String ldapRef;
	private String userDir;
	private String roleAttr;
	private String roleDesc;
	private String userIdAttr;
	private String userFullNameAttr;			//KL 020114
	private String mailAttr ;
	
	private String circaVirtual;
	private String circaUrl;
	private String circaPublicUrl; //KL030530
	private String circaRolePref;
	private String circaRoleSuff;
	
	private String circaOrgPref;
	private String circaOrgPref2;
	private String circaSite;
	
	private String systemUser;
	private String systemPwd;
	
	private String orgDir;
	private String orgIdAttr;
	
	private DirContext ctx = null;
	
	/**
	 * 
	 * @throws DirServiceException
	 */
	public DirectoryService25Impl() throws DirServiceException {
		
		FileServiceIF fsrv = new FileServiceImpl(); 
		
		ldapUrl = fsrv.getStringProperty(FileServiceIF.LDAP_URL);
		ldapCtx =fsrv.getStringProperty(FileServiceIF.LDAP_CTX);
		ldapRef = fsrv.getStringProperty(FileServiceIF.LDAP_REF);
		roleAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_ROLENAME );
		roleDesc = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_ROLEDESC );
		userIdAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_USERID );
		userDir = fsrv.getStringProperty(FileServiceIF.LDAP_USER_DIR );
		mailAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_MAIL );
		userFullNameAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_FULLNAME);
		
		circaVirtual = fsrv.getStringProperty(FileServiceIF.CIRCA_VCIRCA);
		circaUrl = fsrv.getStringProperty(FileServiceIF.CIRCA_URL_MEMBERS);
		circaPublicUrl = fsrv.getStringProperty(FileServiceIF.CIRCA_URL_PUBLIC);
		circaRolePref = fsrv.getStringProperty(FileServiceIF.CIRCA_ROLE_FN_PREFIX );
		circaRoleSuff = fsrv.getStringProperty(FileServiceIF.CIRCA_ROLE_FN_SUFFIX );    
		
		orgIdAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_ORGID);
		orgDir = fsrv.getStringProperty(FileServiceIF.LDAP_ORGANISATION_DIR);
		circaOrgPref=fsrv.getStringProperty(FileServiceIF.CIRCA_ORG_FN_PREFIX);
		circaOrgPref2=fsrv.getStringProperty(FileServiceIF.CIRCA_ORG_FN_PREFIX2);
		circaSite=fsrv.getStringProperty(FileServiceIF.CIRCA_SITE);
		
		try {
			ldapBackUpUrl=fsrv.getStringProperty(FileServiceIF.LDAP_BACKUP);
		}
		catch (DirServiceException e ) {
		}
	}
	
	/**
	 * Creating directory context
	 * Anonymous login to the LDAP server
	 */
	private DirContext sessionLogin() throws DirServiceException {
		
		Hashtable env = new Hashtable();
		String ldapCtxUrl = ldapUrl + ldapCtx;
		env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
		env.put(Context.PROVIDER_URL, ldapCtxUrl);
		env.put(Context.REFERRAL, ldapRef);
		
		DirContext aCtx = null;
		try {
			aCtx = new InitialDirContext(env);
		}
		catch (CommunicationException ce) {
			//try to connect the backup LDAP
			if (ldapBackUpUrl!=null) {
				ldapCtxUrl = ldapBackUpUrl + ldapCtx;
				env.put(Context.PROVIDER_URL, ldapCtxUrl);
				try {
					aCtx = new InitialDirContext(env);
				} catch (NamingException ne) {
					throw new DirServiceException("Exception creating backup LDAP context: " + ne.toString());
				}
			}
			else
				throw new SecurityException("Cannot connect to : " + ldapUrl+ " "  + ce.toString());          
			
		}
		catch (NamingException ne) {
			throw new DirServiceException("Exception, creating LDAP context: " + ne.toString());
		}
		
		return aCtx;
	}
	
	/**
	 * Closes allocated directory context.
	 */
	public void close() {
		if (ctx!=null){
			try {
				ctx.close();
			}
			catch (NamingException ne){
			}
			ctx = null;
		}
	}
	
	
	/**
	 * Gets the role for the given ID.
	 *
	 * @param String roleID
	 * @return Role
	 */
	public Hashtable getRole(String roleID) throws DirServiceException {
		
		String searchFilter;
		Hashtable role = null;
		
		if (ctx == null)
			ctx = sessionLogin();
		
		try {
			searchFilter="(&(objectclass=groupOfUniqueNames)(" + roleAttr + "=" + roleID + "))";
			NamingEnumeration searchResults = searchSubTree(ctx, searchFilter);
			//KL021031
			if (searchResults!=null && searchResults.hasMore()){
				
				SearchResult sr = (SearchResult)searchResults.next();
				
				String roleName = (String)sr.getAttributes().get(roleAttr).get();
				String description="", mail="";
				try {
					Attribute descAttr = (Attribute)sr.getAttributes().get(roleDesc);
					if (descAttr != null )
						description = (String)descAttr.get();
					
					Attribute mAttr = (Attribute)sr.getAttributes().get(mailAttr);              
					if (mAttr != null)
						mail = (String)mAttr.get();
					
				}
				catch (NullPointerException ne) {
				}
				
				Attribute uniqueMember = null;          
				try {
					uniqueMember = sr.getAttributes().get("uniqueMember");
				}
				catch (Exception e ){
					throw new DirServiceException("Error getting occupants for role : " + roleID + "\n" + e.toString());
				}
				
				Vector occupants = parseOccupants(uniqueMember);
				
				role = new Hashtable();
				role.put(ROLE_ID_ATTR, roleID );
				role.put(ROLE_NAME_ATTR, roleName );
				role.put(ROLE_MAIL_ATTR, mail );
				role.put(ROLE_DESCRIPTION_ATTR, description );
				role.put(ROLE_URL_ATTR, getPublicRoleUrl( roleID ) ); 
				role.put(ROLE_MEMBERS_URL_ATTR, getMembersRoleUrl( roleID ) ); 
				role.put(ROLE_OCCUPANTS_ATTR, occupants);
			} //end if searchResults.hasMore()
			else
				throw new DirServiceException("No role in directory " + roleID );
			
		}
		catch (NoSuchElementException nose){
		}
		catch (NamingException ne) {
			throw new DirServiceException("NamingException, if getting role information for role ID= " + roleID + ": " + ne.toString());
		}
		catch (Exception e) {
			throw new DirServiceException("Getting role information for role ID= " + roleID + " failed : " + e.toString());
		}
		
		return role;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param searchFilter
	 * @return
	 * @throws DirServiceException
	 */
	private NamingEnumeration searchSubTree(DirContext ctx, String searchFilter) throws DirServiceException {
		NamingEnumeration ne = searchSubTree(ctx, searchFilter, null);
		return ne;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param filter
	 * @param attrIDs
	 * @return
	 * @throws DirServiceException
	 */
	private NamingEnumeration searchSubTree(DirContext ctx, String filter, String[] attrIDs) throws DirServiceException {
		NamingEnumeration ne = searchSubTree(ctx, "", filter, attrIDs);
		return ne;
	}
	
	/**
	 * 
	 * @param ctx
	 * @param name
	 * @param filter
	 * @param attrIDs
	 * @return
	 * @throws DirServiceException
	 */
	private NamingEnumeration searchSubTree(DirContext ctx, String name, String filter, String[] attrIDs) throws DirServiceException {
		NamingEnumeration ne = null;
		
		try	{
			SearchControls ctls = new SearchControls();
			
			if ( attrIDs != null)
				ctls.setReturningAttributes(attrIDs);
			
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration answer = ctx.search(name, filter, ctls);
			ne = answer;
		}
		catch (NamingException e){
			throw new DirServiceException("Failed searching objects with this filter: " + filter);
		}
		
		return ne;
	}
	
	/**
	 * 
	 */
	public String getPublicRoleUrl(String role) throws DirServiceException {
		return getRoleUrl(role, true);
	}
	
	/**
	 * 
	 */
	public String getMembersRoleUrl(String role) throws DirServiceException {
		return getRoleUrl(role, false);
	}
	
	/**
	 * Returns url of the role in CIRCA
	 * @param String role: ID of the role
	 * @param boolean isPublic: public UL or non-public URL
	 * @return String
	 */
	private String getRoleUrl(String role, boolean isPublic) throws DirServiceException {
		
		StringBuffer url = new StringBuffer();
		if (!isPublic)
			url.append(circaUrl);
		else
			url.append(circaPublicUrl);
		
		url.append(circaVirtual).append(circaRolePref).append(role).append(circaRoleSuff);
		
		return url.toString();
	}
	
	/**
	 * Returns mail addresses of the role
	 * @param String role: ID of the role
	 * @return String
	 */
	public String getRoleMailAddress(String roleID) throws DirServiceException {
		Hashtable role = getRole(roleID);
		return (String)role.get(ROLE_MAIL_ATTR);
	}
	
	/**
	 *	Tries to login with given credentials to the LDAP server
	 *  @param String userID, String userPwd
	 *
	 *  @throws SecurityException if no access, ServiceException
	 */
	public void sessionLogin(String userID, String userPwd) throws DirServiceException, SecurityException {
		
		if (userPwd==null || userPwd.length()==0)
			throw new SecurityException("Authorisation failed: user password cannot be empty");
		
		Hashtable env = new Hashtable();
		
		String ldapUser;
		String ldapCtxUrl = ldapUrl + ldapCtx;
		env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
		env.put(Context.PROVIDER_URL, ldapCtxUrl);
		env.put(Context.REFERRAL, ldapRef);
		ldapUser = userIdAttr + "=" + userID + "," + userDir + "," + ldapCtx;
		env.put(Context.SECURITY_PRINCIPAL, ldapUser);
		env.put(Context.SECURITY_CREDENTIALS, userPwd);
		
		try {
			ctx = new InitialDirContext(env);
		}
		catch (CommunicationException commE) {
			if (ldapBackUpUrl!=null) {
				ldapCtxUrl = ldapBackUpUrl + ldapCtx;
				env.put(Context.PROVIDER_URL, ldapCtxUrl);
				try {
					ctx = new InitialDirContext(env);
				}
				catch (AuthenticationException authe) {
					throw new DirServiceException("Authentication failed: " + authe.toString());
				}
				catch (NamingException ne) {
					throw new DirServiceException("Exception reating backup LDAP context: " + ne.toString());
				}
			}
			else
				throw new SecurityException("Cannot connect to : " + ldapUrl+ " "  + commE.toString());          
		}
		catch (AuthenticationException authe) {
			throw new DirServiceException("Authentication failed: " + authe.toString());
		}
		catch (NamingException ne) {
			throw new SecurityException("Authentication failed: " + ne.toString());
		}
		catch (Exception e){
			throw new SecurityException("Error creating ldap context: " + e.toString());
		}
	}
	
	/**
	 * 
	 */
	public Vector getOccupants(String roleID)    throws DirServiceException {
		if (ctx == null)
			ctx = sessionLogin();
		Hashtable role = getRole(roleID);
		return (Vector)role.get(ROLE_OCCUPANTS_ATTR);
	}
	
	/**
	 * Gets the roles for the given user.
	 *
	 * @param String userID
	 */
	public Vector getRoles(String userID) throws DirServiceException {

		if (ctx == null)
			ctx = sessionLogin();

		String searchFilter;
		Vector roles = new Vector();
		try {
			searchFilter="(objectclass=groupOfUniqueNames)";
			NamingEnumeration searchResults = searchSubTree(ctx, "ou=Roles", searchFilter, null);
			while (searchResults != null && searchResults.hasMore()){
				
				SearchResult sr = (SearchResult)searchResults.next();
				String roleName = (String)sr.getAttributes().get(roleAttr).get();
				Attribute uniqueMember = sr.getAttributes().get("uniqueMember");
				if (uniqueMember!=null){
					Vector v = parseOccupants(uniqueMember);
					if (v!=null && v.contains(userID))
						roles.add(roleName);
				}
			}
			
			if (roles.size()==0)
				throw new DirServiceException("No roles specified for user " + userID );
		}
		catch (NamingException ne) {
			throw new DirServiceException("Getting roles for user " + userID + " failed : " + ne.toString());
		}
		catch (NullPointerException nue) {
			throw new DirServiceException("Getting roles for user " + userID + " failed : " + nue.toString());
		}
		catch (Exception e ){
			throw new DirServiceException("Getting roles for user " + userID + " failed : " + e.toString());      
		}
		
		return roles;
	}
	
	/**
	 * 
	 * @param um
	 * @return
	 * @throws DirServiceException
	 */
	private Vector parseOccupants(Attribute um) throws DirServiceException {
		
		Vector userNames = new Vector();
		if (um == null )
			return userNames;
		
		try {
			String pattern1 = "uid=";
			NamingEnumeration nu   = um.getAll();
			while(nu.hasMore()){
				String s = (String)(nu.next());
				int pos1 = s.indexOf(pattern1);
				if (pos1>=0){
					int pos2 = s.indexOf(",", pos1 + pattern1.length());
					if (pos2<0)
						s = s.substring(pos1 + pattern1.length()).trim();
					else
						s = s.substring(pos1 + pattern1.length(), pos2).trim();
					if (s.length()>0)
						userNames.add(s);
				}
			}
		}
		catch (NamingException ne) {
			throw new DirServiceException( "Error getting user info" + ne.toString());
		}
		catch (NullPointerException nu){
			throw new DirServiceException( "Error getting user info" + nu.toString());
		}
		
		return userNames;
	}
	
	/**
	 * 
	 */
	public Hashtable getPerson (String uId) throws DirServiceException {
		
		DirContext tmpCtx = (ctx != null) ?	ctx : sessionLogin();
		String fullName = uId, orgId="";
		Hashtable person=new Hashtable();
		
		try {
			Attributes pAttrs = tmpCtx.getAttributes("uid=" + uId + ", " + userDir  );
			Attribute fName = pAttrs.get(userFullNameAttr);
			if (fName!=null)
				fullName = fName.get().toString();
			
			Attribute o = pAttrs.get("o");      
			if (o!=null) { //format orgid@site
				orgId = o.get().toString() ;
				if (orgId.indexOf("@") != -1)
					orgId = orgId.substring(0, orgId.indexOf("@"));
			}
			
			person.put(PERSON_UID_ATTR, uId);
			person.put(PERSON_FULLNAME_ATTR, fullName);
			person.put(PERSON_ORGID_ATTR, orgId);
		}
		catch (NameNotFoundException nf ){
			throw new DirServiceException("No such user: " + uId );
		}
		catch (NamingException ne) {
			throw new DirServiceException("Error getting full name for user: " + uId );
		} 
		
		return person;
	}
	
	/**
	 * Returns Full Name ( forename + surname ) of the user
	 * @param String user: login ID of the user
	 * @return String
	 */
	public String getFullName(String user) throws DirServiceException {
		
		String fullName=user;
		Hashtable person=getPerson(user);
		fullName=(String)person.get(PERSON_FULLNAME_ATTR);
		return fullName;
		
	}

	/**
	 * 
	 */
	public Vector listOrganisations() throws DirServiceException {
		
		Vector v = new Vector();
		DirContext tmpCtx = (ctx != null) ?	ctx : sessionLogin();
		NamingEnumeration ne = null;
		try {
			ne = tmpCtx.list(orgDir);
			if (ne==null)
				throw new DirServiceException("No organisations exist in directory specified: " + orgDir );
			
			while (ne.hasMore()) {
				NameClassPair ncp = (NameClassPair)ne.next();
				String orgId = ncp.getName();
				orgId=orgId.substring( orgId.indexOf(orgIdAttr)+ orgIdAttr.length() +1  );
				v.add(orgId);
			}
		}
		catch (NamingException nex) {
			throw new DirServiceException("Error listing organisations " + nex.toString());
		}
		
		return v;
		
	}
	
	/**
	 * Finds an attribute from search results and returns the value, if null, returns 
	 * an empty String
	 */
	private String getAttributeValue(SearchResult sr, String name) throws DirServiceException {
		
		String value="";
		try {
			Attribute attr = (Attribute)sr.getAttributes().get(name);
			if (attr != null )
				value = (String)attr.get();
		}
		catch (NamingException ne ) {
			throw new DirServiceException("Error getting attribute value = " + name + "\n" + ne.toString());
		}
		
		return value;
	}
	
	/**
	 * 
	 */
	public Hashtable getOrganisation(String orgId) throws DirServiceException {
		
		Hashtable org = null;
		String searchFilter = null;
		try {
			if (ctx == null)
				ctx = sessionLogin();
			
			searchFilter="(&(objectclass=groupOfUniqueNames)(cn=" + orgId + "))";
			NamingEnumeration searchResults = searchSubTree(ctx, searchFilter);
			if (searchResults!=null && searchResults.hasMore()) {
				
				SearchResult sr = (SearchResult)searchResults.next();
				
				String name =  getAttributeValue(sr,"givenname"); 
				String description = getAttributeValue(sr,"description");
				String mail = getAttributeValue(sr,"mail");
				String phone = getAttributeValue(sr,"telephonenumber");
				String fax = getAttributeValue(sr,"fax");
				String country = getAttributeValue(sr,"c");
				String homepage=getAttributeValue(sr,"labeleduri");
				String address=getAttributeValue(sr,"street") + "  " + getAttributeValue(sr,"l") + " " + getAttributeValue(sr,"postalcode");
				String bCategory=getAttributeValue(sr,"businesscategory");
				
				Attribute uniqueMember = sr.getAttributes().get("uniquemember");
				Vector users = parseOccupants(uniqueMember);
				
				String url = getOrgUrl(orgId, false);
				
				org = new Hashtable();
				org.put(ORG_ID_ATTR, orgId);
				org.put(ORG_NAME_ATTR, name);
				org.put(ORG_ADDRESS_ATTR, address);
				org.put(ORG_BCATEGORY_ATTR, bCategory);          
				org.put(ORG_COUNTRY_ATTR, country);
				org.put(ORG_DESCRIPTION_ATTR, description);
				org.put(ORG_FAX_ATTR, fax);
				org.put(ORG_HOMEPAGE_ATTR, homepage);
				org.put(ORG_PHONE_ATTR, fax);          
				org.put(ORG_OCCUPANTS_ATTR, users);
				
				org.put(ORG_URL_ATTR, url);
			} //end if searchResults.hasMore()
			else
				throw new DirServiceException("No such organisation in directory: " + orgId );
		}
		catch (NoSuchElementException nose){
		}
		catch (NamingException ne) {
			throw new DirServiceException("NamingException when getting information for organisation (ID= " + orgId + "): " + ne.toString());
		}
		catch (Exception e) {
			throw new DirServiceException("Failed getting information for organisation ID=" + orgId + ": " + e.toString());
		}
		
		return org;
	}
	
	/**
	 * 
	 * @param org
	 * @param isPublic
	 * @return
	 * @throws DirServiceException
	 */
	private String getOrgUrl(String org, boolean isPublic) throws DirServiceException {
		
		StringBuffer url = new StringBuffer();
		if (!isPublic)
			url.append(circaUrl);
		else
			url.append(circaPublicUrl);
		url.append(circaVirtual).append(circaOrgPref).append(org).append(circaOrgPref2).append(circaSite);
		
		return url.toString();
		
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args){
	
		try{
			DirectoryService25Impl dirService = new DirectoryService25Impl();
			Vector orgs = dirService.getRoles("binosil");
			System.out.println(orgs);
		}
		catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
}
