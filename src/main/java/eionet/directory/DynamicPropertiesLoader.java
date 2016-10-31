package eionet.directory;

import java.util.Hashtable;

public final class DynamicPropertiesLoader {
     
    public static DynamicProperties dynamicProperties;  

    public static void setDynamicProperties(DynamicProperties dynamicProperties) {
        DynamicPropertiesLoader.dynamicProperties = dynamicProperties;
    }
    
    public static Hashtable getDynamicProperties() {
        Hashtable returnProps = new Hashtable();
        if (dynamicProperties != null) {
            if (dynamicProperties.getLdapPassword()!= null) {
                returnProps.put(FileService.LDAP_PASSWORD, dynamicProperties.getLdapPassword());
            }
            if (dynamicProperties.getLdapPrincipal()!= null) {
                returnProps.put(FileService.LDAP_PRINCIPAL, dynamicProperties.getLdapPrincipal());
            }
            if (dynamicProperties.getLdapUrl()!= null) {
                returnProps.put(FileService.LDAP_URL, dynamicProperties.getLdapUrl());
            }
        }
        return returnProps;
    }
    
}