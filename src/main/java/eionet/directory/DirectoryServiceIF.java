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

package eionet.directory;

import java.util.Hashtable;
import java.util.Vector;

import eionet.directory.dto.RoleDTO;

/**
 * Method declarations for directory services.
 *
 * @author  Kaido Laine
 * @version 1.1
 */
public interface DirectoryServiceIF {

    /*
     * Closes the opened context object
     * @throws ServiceException
     */
    // public void close() throws ServiceException;

    /*
     * Returns the roles of the user
     * @param roleID
     * @return String
     * @deprecated
     */
    // public String getRoleUrl(String roleID) throws DirServiceException;

    @Deprecated
    public final String ROLE_ID_ATTR = "ID";
    public final String ROLE_NAME_ATTR = "NAME";
    public final String ROLE_MAIL_ATTR = "MAIL";
    public final String ROLE_DESCRIPTION_ATTR = "DESCRIPTION";
    public final String ROLE_URL_ATTR = "URL";
    public final String ROLE_MEMBERS_URL_ATTR = "URL_MEMBERS";
    public final String ROLE_OCCUPANTS_ATTR = "OCCUPANTS";

    public final String ORG_ID_ATTR = "ID";
    public final String ORG_NAME_ATTR = "NAME";
    public final String ORG_DESCRIPTION_ATTR = "DESCRIPTION";
    public final String ORG_BCATEGORY_ATTR = "BUSINESSCATEGORY";
    public final String ORG_ADDRESS_ATTR = "ADDRESS";
    public final String ORG_COUNTRY_ATTR = "COUNTRY";
    public final String ORG_MAIL_ATTR = "MAIL";
    public final String ORG_PHONE_ATTR = "PHONE";
    public final String ORG_FAX_ATTR = "FAX";
    public final String ORG_OCCUPANTS_ATTR = "OCCUPANTS";
    public final String ORG_HOMEPAGE_ATTR = "HOMEPAGE";

    public final String PERSON_UID_ATTR = "UID";
    public final String PERSON_FULLNAME_ATTR = "FULLNAME";
    public final String PERSON_ORGID_ATTR = "ORG_ID";

    // Url in Circa for logged in users
    public final String ORG_MEMBERS_URL_ATTR = "URL_MEMBERS";
    // URL in Circa for public access
    public final String ORG_URL_ATTR = "URL";

    /**
     * Email of the given role.
     *
     * @param roleId - the CN attribute for the role in the directory.
     * @throws DirServiceException if something went wrong
     * @return email as a string.
     */
    public String getRoleMailAddress(String roleId) throws DirServiceException;

    /**
     * Lookup person with the given uid.
     * Miscellaneous data in a HASH.
     *
     * @param uId account name
     * @return person attributes as a hashtable.
     * @throws DirServiceException if something went wrong
     */
    public Hashtable<String, String> getPerson(String uId) throws DirServiceException;

    /**
     * Role of the given ID.
     *
     * @param roleId - the CN attribute for the role in the directory.
     * @throws DirServiceException if something went wrong
     * @return role attributes as a hashtable.
     */
    public Hashtable<String, Object> getRole(String roleId) throws DirServiceException;

    /**
     * Role of the given ID.
     *
     * @param roleId - the CN attribute for the role in the directory.
     * @throws DirServiceException if something went wrong
     * @return role attributes as DTO
     */
    public RoleDTO getRoleDTO(String roleId) throws DirServiceException;

    /**
     * Login to the LDAP server with credentials.
     *
     * @param userID account id
     * @param userPwd password
     * @throws DirServiceException if something went wrong
     * @throws SecurityException if login is not allowed
     */
    public void sessionLogin(String userID, String userPwd) throws DirServiceException, SecurityException;

    /**
     * Returns the roles of the user.
     *
     * @param userID account name
     * @throws DirServiceException if something went wrong
     * @return Vector, contains role names
     */
    public Vector<String> getRoles(String userID) throws DirServiceException;

    /**
     * Returns the occupants of the role.
     *
     * @param roleID role id
     * @throws DirServiceException if something went wrong
     * @return Vector, contains user (login) names
     */
    public Vector<String> getOccupants(String roleID) throws DirServiceException;

    /**
     * Returns the full name of the user.
     *
     * @param userID account name
     * @throws DirServiceException if something went wrong
     * @return Full name as string.
     */
    public String getFullName(String userID) throws DirServiceException;

    /**
     * Lists organisation IDs in LDAP Organisation folder.
     * folder specified in eionetdir.properties ldap.organisation.dir
     * @throws DirServiceException if something went wrong
     * @return list of organisation names
     */
    public Vector<String> listOrganisations() throws DirServiceException;

    /**
     * Returns an Organisation in HASH.
     * includes STRING attributes:
     * ID, NAME, ADDRESS, BUSINESSCATEGORY, HOMEPAGE, COUNTRY, PHONE, FAX,
     * DESCRIPTION, MAIL
     * ARRAY attribute: OCCUPANTS - includes user names of organisation members
     * URL: Url in Circa for members' access
     * @param orgId - the <b>cn</b> of the organisation.
     * @throws DirServiceException if there is no matching organisation.
     * @return the Organisation in HASH.
     */
    public Hashtable<String, Object> getOrganisation(String orgId) throws DirServiceException;

}
