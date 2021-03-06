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
 * Copyright (c) 2000-2015 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Rando Valt (TietoEnator)
 */

package eionet.directory;

/**
 * Methods for file operations.
 *
 * @author  Søren Roug
 * @version 3.0
 */
public interface FileService {

    /**
     * LDAP server URL.
     */
    String LDAP_URL = "ldap.url";

    /**
     * LDAP backup server URL.
     */

    String LDAP_BACKUP = "ldap.backup.url";
    /**
     * LDAP context.
     */
    String LDAP_CTX = "ldap.context";

    /**
     * Directory, where users are held.
     */
    String LDAP_USER_DIR = "ldap.user.dir";

    /**
     * Directory, where organisations are held.
     */
    String LDAP_ORGANISATION_DIR = "ldap.organisation.dir";

    /**
     * Attribute of the role object name.
     */
    String LDAP_ATTR_ROLENAME = "ldap.attr.rolename";

    /**
     * attribute for organisation ID.
     */
    String LDAP_ATTR_ORGID = "ldap.attr.orgid";

    /**
     * attribute for organisation name.
     */
    String LDAP_ATTR_ORGNAME = "ldap.attr.orgname";

    /**
     * attribute for organisation URL.
     */
    String LDAP_ATTR_ORGURL = "ldap.attr.orgurl";

    /**
     * Attribute of the role object description.
     */
    String LDAP_ATTR_ROLEDESC = "ldap.attr.roledescription";

    /**
     * Attribute of the user object, where e-mail address is held.
     */
    String LDAP_ATTR_MAIL = "ldap.attr.mail";

    /**
     * Attribute, holding the full name of the user.
     */
    String LDAP_ATTR_FULLNAME = "ldap.attr.fullname";

    /**
     * User ID attribute in LDAP.
     */
    String LDAP_ATTR_USERID = "ldap.attr.uid";

    /**
     * Referral property of Initial context (maybe not needed...).
     */
    String LDAP_REF = "ldap.ref";

    /**
     * The principal for logging onto LDAP (see javax.naming.Context.SECURITY_PRINCIPAL).
     * The authentication mechanism will be "simple" (see javax.naming.Context.SECURITY_AUTHENTICATION).
     */
    String LDAP_PRINCIPAL = "ldap.principal";

    /**
     * The password for the principal identified by ldap.principal. See see javax.naming.Context.SECURITY_CREDENTIALS.
     */
    String LDAP_PASSWORD = "ldap.password";

    /**
     * The URL of the Role site in Eionet Directory.
     */
    String LDAP_ROLE_SITE_URL = "ldap.role.site.url";

    /**
     * Lookup a property. The property must exist.
     *
     * @param propName - name of property.
     * @return String type property from the properties file.
     * @throws DirServiceException if the property doesn't exist.
     */
    String getStringProperty(String propName) throws DirServiceException;


    /**
     * Returns String type property from the properties file if exists. Otherwise empty String is returned.
     *
     * @param propName Property name
     * @return String property value.
     */
    String getOptionalStringProperty(String propName);

}
