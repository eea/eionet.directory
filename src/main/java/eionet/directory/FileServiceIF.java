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
 * Original Code: Rando Valt (TietoEnator)
 */

package eionet.directory;

/**
 * Methods for file operations.
 *
 * @author  Rando Valt
 * @version 1.1
 */
public interface FileServiceIF {

    /**
     * LDAP server URL
     */
    public static final String LDAP_URL = "ldap.url";

    /**
     * LDAP backup server URL
     */

    public static final String LDAP_BACKUP = "ldap.backup.url";
    /**
     * LDAP context
     */
    public static final String LDAP_CTX = "ldap.context";

    /**
     * Directory, where users are held
     */
    public static final String LDAP_USER_DIR = "ldap.user.dir";

    /**
     * Directory, where organisations are held
     */
    public static final String LDAP_ORGANISATION_DIR = "ldap.organisation.dir";

    /**
     * Attribute of the role object name
     */
    public static final String LDAP_ATTR_ROLENAME = "ldap.attr.rolename";

    /**
     * attribute for organisation ID
     */
    public static final String LDAP_ATTR_ORGID = "ldap.attr.orgid";

    /**
     * attribute for organisation name
     */

    public static final String LDAP_ATTR_ORGNAME = "ldap.attr.orgname";
    /**
     * attribute for organisation URL
     */
    public static final String LDAP_ATTR_ORGURL = "ldap.attr.orgurl";

    /**
     * Attribute of the role object description
     */

    public static final String LDAP_ATTR_ROLEDESC = "ldap.attr.roledescription";
    /**
     * Attribute of the user object, where e-mail address is held
     */
    public static final String LDAP_ATTR_MAIL = "ldap.attr.mail";
    /**
     * Attribute, holding the full name of the user
     */
    public static final String LDAP_ATTR_FULLNAME = "ldap.attr.fullname";
    /**
     * User ID attribute in LDAP
     */
    public static final String LDAP_ATTR_USERID = "ldap.attr.uid";
    /**
     * Referral property of Initial context (maybe not needed...)
     */
    public static final String LDAP_REF = "ldap.ref";

    /**
     * Returns String type property from the properties file
     */
    public String getStringProperty(String propName) throws DirServiceException;

    /**
     * Returns boolean type property from the properties file
     */
    public boolean getBooleanProperty(String propName) throws DirServiceException;

    /**
     * Returns int type property from the properties file
     */
    public int getIntProperty(String propName) throws DirServiceException;

}
