/**
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

package eionet.directory;

import java.util.Hashtable;
import java.util.Vector;

import eionet.directory.dto.RoleDTO;
import eionet.directory.modules.DirectoryService25Impl;

/**
 * Adapter class for the Directoryservice
 * All methods are static in this class
 * @author Kaido Laine
 * @version 1.1
 */

public class DirectoryService {

	private static DirectoryServiceIF dir;

	/**
	 * Email of the role
	 */
	public static String getRoleMail( String roleID ) throws DirServiceException {

		if (dir ==null)
			dir = getDirService();

		return dir.getRoleMailAddress(roleID);

	}

	/**
	 * Roles, where the user belongs to
	 */
	public static Vector<String> getRoles( String userID ) throws DirServiceException {

		if (dir ==null)
			dir = getDirService();

		return dir.getRoles( userID ) ;

	}

	/**
	 *
	 */
	public static Hashtable<String,Object> getRole(String roleID ) throws DirServiceException {

		if (dir ==null) 
			dir = getDirService();

		Hashtable<String,Object> role = dir.getRole(roleID);

		return role;

	}

	/**
	 *
	 */
	public static RoleDTO getRoleDTO(String roleID ) throws DirServiceException {

		if (dir ==null) 
			dir = getDirService();

		RoleDTO role = dir.getRoleDTO(roleID);

		return role;

	}

	/**
	 * returns new instance of DirectoryServiceIF, based on CIRCA version
	 */
	private static DirectoryServiceIF getDirService () throws DirServiceException {
		return new DirectoryService25Impl();
	}


	public static void sessionLogin( String user, String pwd ) throws DirServiceException {
		if (dir ==null)
			dir = getDirService();

		dir.sessionLogin( user, pwd );

	}

	public static String getFullName( String userID )    throws DirServiceException {
		if (dir ==null)
			dir = getDirService();
		return dir.getFullName(userID);
	}

	public static Vector<String> getOccupants( String roleID )    throws DirServiceException {
		if (dir ==null)
			dir = getDirService();
		return dir.getOccupants(roleID);
	}

	public static Vector<String> listOrganisations() throws DirServiceException {
		if (dir ==null)
			dir = getDirService();
		return dir.listOrganisations();

	}

	public static Hashtable<String,String> getPerson(String uId) throws DirServiceException {
		if (dir ==null)
			dir = getDirService();
		return dir.getPerson(uId);

	}


	public static Hashtable<String,Object> getOrganisation(String orgId) throws DirServiceException {
		if (dir ==null)
			dir = getDirService();
		return dir.getOrganisation(orgId);
	}

}