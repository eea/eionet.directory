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
import java.util.List;
import java.util.Vector;

import eionet.directory.dto.RoleDTO;
import eionet.directory.modules.DirectoryService25Impl;
import eionet.directory.modules.FileServiceImpl;

/**
* Adapter class for the Directoryservice
* All methods are static in this class
* @author Kaido Laine
* @version 1.1
*/

public class DirectoryService {

 private static DirectoryServiceIF dir;
 private static FileServiceIF fSrv;


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
 public static Vector getRoles( String userID ) throws DirServiceException {

  if (dir ==null)
    dir = getDirService();

  return dir.getRoles( userID ) ;
  
 }

/**
*
*/
public static Hashtable getRole(String roleID ) throws DirServiceException {

  if (dir ==null) 
    dir = getDirService();
  
  Hashtable role = dir.getRole(roleID);

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
 * URL of the role for dataflow
 */
 public static String getRoleUrl( String roleID ) throws DirServiceException {
/*
  if (dir ==null)
    dir = getDirService();

  String url = dir.getMembesRoleUrl(roleID);
*/
  return getMembersRoleUrl(roleID);
  
  
 }

 /**
 * URL of the role for dataflow
 */
 public static String getMembersRoleUrl( String roleID ) throws DirServiceException {

  if (dir ==null)
    dir = getDirService();

  String url = dir.getMembersRoleUrl(roleID);

  return url;
  
  
 }

 /**
 * URL of the role for dataflow
 */
 public static String getPublicRoleUrl( String roleID ) throws DirServiceException {

  if (dir ==null)
    dir = getDirService();

  String url = dir.getPublicRoleUrl(roleID);

  return url;
  
  
 }



  /**
  * returns new instance of DirectoryServiceIF, based on CIRCA version
  */
  private static DirectoryServiceIF getDirService () throws DirServiceException {
    //int version  = 
    if (fSrv == null)
      fSrv = getFileService();
      
    String cVer = fSrv.getStringProperty( fSrv.CIRCA_VERSION );
    if (cVer.charAt(0) == '2' )
      return new DirectoryService25Impl();
    else
      throw new DirServiceException("Not implemented yet");
 }

  private static FileServiceIF getFileService() throws DirServiceException {
    return new FileServiceImpl();    
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

    public static Vector getOccupants( String roleID )    throws DirServiceException {
    if (dir ==null)
      dir = getDirService();
      return dir.getOccupants(roleID);
    }

    public static Vector listOrganisations() throws DirServiceException {
      if (dir ==null)
        dir = getDirService();
     return dir.listOrganisations();
     
    }

    public static Hashtable getPerson(String uId) throws DirServiceException {
      if (dir ==null)
        dir = getDirService();
     return dir.getPerson(uId);
     
    }


    public static Hashtable getOrganisation(String orgId) throws DirServiceException {
      if (dir ==null)
        dir = getDirService();
     return dir.getOrganisation(orgId);
    }
    

/*public static void main(String args[]) {
  try {
      if (dir ==null)
        dir = getDirService();


  } catch (Exception e ) {
    System.out.println("e= " + e.toString());
  }
 }  */

}