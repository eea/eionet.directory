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

//test->
import javax.naming.NameClassPair;
//<-
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

//import com.tee.util.Util;


/**
 * Provides functionality related to LDAP directory server as login,
 * getting roles and getting e-mail addresses for users of a certain role.
 *
 * @author  Kaido Laine
 * @version 1.0
 */


public class DirectoryService25Impl implements DirectoryServiceIF {
  
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
  //private LogServiceIF logger;
  
  private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
  
    /** Creates new LdapServiceImpl */
  public DirectoryService25Impl() throws DirServiceException {
    FileServiceIF fsrv = new FileServiceImpl(); // CSServices.getFileService();
   
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
    } catch (DirServiceException e ) {
      //not found, support the version with backup server not specified
    }
    //KL020423
    
    //orgNameAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_ORGNAME);
    //orgUrlAttr = fsrv.getStringProperty(FileServiceIF.LDAP_ATTR_ORGURL);    
    
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
    } catch (CommunicationException ce) {
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
  
    } catch (NamingException ne) {
      throw new DirServiceException("Exception, creating LDAP context: " + ne.toString());
    }
if(aCtx==null)
  _log("aa = null");
    return aCtx;
  }
/*  private void createBackup(DirContext bCtx, Hashtable env) throws DirServiceException {
      if (ldapBackUpUrl!=null) {
        String ldapCtxUrl = ldapBackUpUrl + ldapCtx;
_log(ldapCtxUrl)        ;

        env.put(Context.PROVIDER_URL, ldapCtxUrl);
        try {
          bCtx = new InitialDirContext(env);
if(bCtx==null)
  _log("=== null");
        } catch (NamingException ne) {
          throw new DirServiceException("Exception reating backup LDAP context: " + ne.toString());
        }
      }

  } */

/**
 * Closes allocated directory context.
 */
  public void close() {
    if (ctx != null)	{
      try {
        ctx.close();
      } catch (NamingException ne) {}
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
      searchFilter="(&(objectclass=Role)(" + roleAttr + "=" + roleID + "))";
      NamingEnumeration searchResults = searchSubTree(ctx, searchFilter);
      //KL021031
      if (searchResults != null && searchResults.hasMore() ) {
           
          SearchResult sr = (SearchResult)searchResults.next();

          String roleName = (String)sr.getAttributes().get(roleAttr).get();
          String description="", mail="";
          //ArrayList users;
          //String uniqueM;
          try {
            Attribute descAttr = (Attribute)sr.getAttributes().get(roleDesc);

            if (descAttr != null )
              description = (String)descAttr.get();

            Attribute mAttr = (Attribute)sr.getAttributes().get(mailAttr);              
            
            if (mAttr != null)
               mail = (String)mAttr.get();

          } catch (NullPointerException ne) {

            ///!!! What's this ???
            //description = "";
            //mail ="";
            //users=null;
          }

          Attribute uniqueMember = null;          
          try {
            uniqueMember = sr.getAttributes().get("uniquemember");
          } catch (Exception e ){
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

          //role.setUsers( users );
          
      } //end if
      else
        throw new DirServiceException("No role in directory " + roleID );
        
    } catch (NoSuchElementException nose){
      //throw new ServiceException("No such role in directory= " + roleID + " " + nose.toString());
      //do nothing, return Role as null    
    } catch (NamingException ne) {
      throw new DirServiceException("NamingException, if getting role information for role ID= " + roleID + ": " + ne.toString());
    } catch (Exception e) {
      throw new DirServiceException("Getting role information for role ID= " + roleID + " failed : " + e.toString());
    }

    return role;
  }

  

//->  


 /**
 * helper methods
 */
  private NamingEnumeration searchSubTree(DirContext ctx, String searchFilter) throws DirServiceException {
    NamingEnumeration ne = searchSubTree(ctx, searchFilter, null);
    
    return ne;
  }
  
  private NamingEnumeration searchSubTree(DirContext ctx, String filter, String[] attrIDs) throws DirServiceException {
    NamingEnumeration ne = null;
    
    try	{
      SearchControls ctls = new SearchControls();
      
      if ( attrIDs != null)
        ctls.setReturningAttributes(attrIDs);
      
      ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      NamingEnumeration answer = ctx.search("", filter, ctls);
      ne = answer;
    } catch (NamingException e)	{
      throw new DirServiceException("Searching objects " + filter + " failed.");
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
 * @DEPRECATED
 */
/* public String getRoleUrl(String role) throws DirServiceException {
  return getRoleUrl(role, true);
 } */
/**
 * Returns url of the role in CIRCA
 * @param String role: ID of the role
 * @param boolean isPublic: public UL or non-public URL
 * @return String
 */

 private String getRoleUrl(String role, boolean isPublic) throws DirServiceException {

 //http://eea.eionet.eu.int:8980/Members/irc/eionet-circle/Home/central_dir_admin?fn=roles&v=eea&rd=0&ud=0&od=0
  StringBuffer url = new StringBuffer();

  if (!isPublic)
    url.append(circaUrl);
  else
    url.append(circaPublicUrl);
    
  url.append(circaVirtual).append(circaRolePref).append(role)
    .append(circaRoleSuff);
  
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


private static void _log(String s){
  System.out.println("************* " + s );
}


/**
 *	Tries to login with given credentials to the LDAP server
 *  @param String userID, String userPwd
 *
 *  @throws SecurityException if no access, ServiceException
 */
  public void sessionLogin(String userID, String userPwd) throws DirServiceException, SecurityException {
    //KL011207 -> have to study LDAP, how to avoid logging with empty passwd
    //logger.debug("sessionLogin start")    ;

    //if (! Util.nullString( userPwd )){
    if (! ( userPwd.equals("") || userPwd == null)  ){
      Hashtable env = new Hashtable();
      
      String ldapUser;
      String ldapCtxUrl = ldapUrl + ldapCtx;

      //logger.debug("LdapUrl=" + ldapCtxUrl);
      env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
      env.put(Context.PROVIDER_URL, ldapCtxUrl);
      env.put(Context.REFERRAL, ldapRef);
      ldapUser = userIdAttr + "=" + userID + "," + userDir + "," + ldapCtx;
      //logger.debug("LdapUser=" + ldapUser);
      env.put(Context.SECURITY_PRINCIPAL, ldapUser);
      env.put(Context.SECURITY_CREDENTIALS, userPwd);
      
      try {
        //logger.debug("going to get context");
        ctx = new InitialDirContext(env);
        //logger.debug("got context");        
      } catch (CommunicationException commE) {
        if (ldapBackUpUrl!=null) {
          ldapCtxUrl = ldapBackUpUrl + ldapCtx;
          env.put(Context.PROVIDER_URL, ldapCtxUrl);
          try {
            ctx = new InitialDirContext(env);
          } catch (AuthenticationException authe) {
            throw new DirServiceException("Authentication failed: " + authe.toString());
          } catch (NamingException ne) {
            throw new DirServiceException("Exception reating backup LDAP context: " + ne.toString());
          }
        }
        else
          throw new SecurityException("Cannot connect to : " + ldapUrl+ " "  + commE.toString());          

      } catch (AuthenticationException authe) {
         throw new DirServiceException("Authentication failed: " + authe.toString());
      } catch (NamingException ne) {
        //logger
        //_log("security exception creating context " + ne.toString() );      
        throw new SecurityException("Authentication failed: " + ne.toString());
      } catch (Exception e){
        //logger.error("error creating context " + e.toString() );
        //_log("error creating context " + e.toString() );
        throw new SecurityException("Error creating ldap context: " + e.toString());
      }
    }
    else 
      throw new SecurityException("Authorisation failed: Password cannot be empty " );

  }
  
   public Vector getOccupants( String roleID )    throws DirServiceException {
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
    
    String searchFilter;
    Vector roles = new Vector();
    //HashMap roles = new HashMap();
    
    //+RV010926
    if (ctx == null)
      throw new DirServiceException("No directory context");
    
    try {
      searchFilter="(&(objectclass=Role)(uniquemember=" + userID +  "@*))";
      NamingEnumeration searchResults = searchSubTree(ctx, searchFilter);
      if (searchResults != null) {
        while (searchResults.hasMore()) {
          SearchResult sr = (SearchResult)searchResults.next();
          String roleName = (String)sr.getAttributes().get(roleAttr).get();

          roles.add( roleName );
          
        }   //end while
      } //end if
      else
        throw new DirServiceException("No roles specified for user " + userID );
    } catch (NamingException ne) {
      throw new DirServiceException("Getting roles for user " + userID + " failed : " + ne.toString());
    } catch (NullPointerException nue) {
      //KL020617
      //_log("null, if getting roles " + nue.toString());
      //logger.warning("null, if getting roles " + nue.toString());
      throw new DirServiceException("Getting roles for user " + userID + " failed : " + nue.toString());
    } catch (Exception e ){
      //logger.warning("error, if getting roles " + e.toString());        
      //_log("error, if getting roles " + e.toString());    
      throw new DirServiceException("Getting roles for user " + userID + " failed : " + e.toString());      
    }
    
    return roles;
  }

 private Vector parseOccupants( Attribute um ) throws DirServiceException {

      Vector userNames = new Vector();
      try {

        if (um == null )
          return userNames;
      
        NamingEnumeration nu   = um.getAll();

        //al = new ArrayList();
        while(nu.hasMore()){
          String uid = (String)(nu.next());
          int pos = uid.indexOf("@");
          if (pos > -1){
            uid = uid.substring( 0, pos ).trim();
            userNames.add(uid);
          }
        }
      } catch (NamingException ne) {
        throw new DirServiceException( "Error getting user info" + ne.toString());
      } catch (NullPointerException nu){
        throw new DirServiceException( "Error getting user info" + nu.toString());
        //no users
        //return null;
      }

    return userNames;
  }


/**
 * Returns Full Name ( forename + surname ) of the user
 * @param String user: login ID of the user
 * @return String
 */
  public String getFullName(String user) throws DirServiceException {
    
    DirContext tmpCtx = (ctx != null) ?	ctx : sessionLogin();
    String fullName = user;
    
    try {
      Attribute attr = tmpCtx.getAttributes("uid=" + user + ", " + userDir  ).get( userFullNameAttr );

      if (attr!=null)
        fullName = attr.get().toString();
/*      else {
        //return login name as full name
        //fullName = user;
        //logger.warning("DirectoryService did not get the full name for user " + user);
      }
*/
        
    } catch (NameNotFoundException nf ){
      throw new DirServiceException("No such user: " + user );
    } catch (NamingException ne) {
      throw new DirServiceException("Error getting full name for user: " + user );
    } 
    
    return fullName;
    
   }

  public Vector listOrganisations() throws DirServiceException {
    Vector v = new Vector();
    DirContext tmpCtx = (ctx != null) ?	ctx : sessionLogin();        

    NamingEnumeration ne = null;

    try {
      ne=tmpCtx.list(orgDir);

      if (ne==null)
        throw new DirServiceException("No organisations exist in directory specified: " + orgDir );
          
      while (ne.hasMore()) {
        NameClassPair ncp = (NameClassPair)ne.next();
        String orgId = ncp.getName();
        orgId=orgId.substring( orgId.indexOf(orgIdAttr)+ orgIdAttr.length() +1  );
        v.add(orgId);
      }
    } catch (NamingException nex) {
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
    } catch (NamingException ne ) {
      throw new DirServiceException("Error getting attribute value = " + name +
        "\n" + ne.toString());
    }

    return value;
  }

public Hashtable getOrganisation(String orgId) throws DirServiceException {
  Hashtable org = null;
  try {

    String searchFilter;


    if (ctx == null)
      ctx = sessionLogin();
      
    searchFilter="(&(objectclass=organisation)(cn=" + orgId + "))";
    NamingEnumeration searchResults = searchSubTree(ctx, searchFilter);
    if (searchResults != null && searchResults.hasMore() ) {
      SearchResult sr = (SearchResult)searchResults.next();

          /*while ( attrNames.hasMore()) {
            Attribute aaaa = (Attribute)attrNames.next();
            //l( (String)aaaa.get());
            l(" " + aaaa);
          } *///<-
          
      String name, description, mail, address, country;
      String url,  homepage, phone, fax, bCategory;
      //String url_members;
      
      Vector users;

      name =  getAttributeValue(sr,"givenname"); // (String)sr.getAttributes().get("givenname").get(); 
      description = getAttributeValue(sr,"description");
      mail = getAttributeValue(sr,"mail");
      phone = getAttributeValue(sr,"telephonenumber");
      fax = getAttributeValue(sr,"fax");
      country = getAttributeValue(sr,"c");
      homepage=getAttributeValue(sr,"labeleduri");

      address=getAttributeValue(sr,"street") + "  " + getAttributeValue(sr,"l") + " " + getAttributeValue(sr,"postalcode");
      bCategory=getAttributeValue(sr,"businesscategory");

      Attribute uniqueMember = null;          
      uniqueMember = sr.getAttributes().get("uniquemember");
      
      users = parseOccupants(uniqueMember);

      url=getOrgUrl(orgId, false);
  
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

      } //end if
      else
        throw new DirServiceException("No such organisation in directory: " + orgId );
        
    } catch (NoSuchElementException nose){
      //throw new ServiceException("No such role in directory= " + roleID + " " + nose.toString());
      //do nothing, return Role as null    
    } catch (NamingException ne) {
      throw new DirServiceException("NamingException, if getting role information for role ID= " + ne.toString());
    } catch (Exception e) {
      throw new DirServiceException("Getting role information for role ID=  failed : " + e.toString());
    }

    return org;
   }

  /* public static void l(String a) {
    System.out.println(a);
  } */

  private String getOrgUrl(String org, boolean isPublic) throws DirServiceException {

    StringBuffer url = new StringBuffer();

    if (!isPublic)
      url.append(circaUrl);
    else
      url.append(circaPublicUrl);
    
    url.append(circaVirtual).append(circaOrgPref).append(org)
      .append(circaOrgPref2).append(circaSite);
  
    return url.toString();

 }

}
