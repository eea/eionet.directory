package eionet.directory;


public class DynamicProperties {
    
    private String ldapUrl;
    private String ldapPrincipal;
    private String ldapPassword;

    public String getLdapUrl() {
        return ldapUrl;
    }

    public void setLdapUrl(String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    public String getLdapPrincipal() {
        return ldapPrincipal;
    }

    public void setLdapPrincipal(String ldapPrincipal) {
        this.ldapPrincipal = ldapPrincipal;
    }

    public String getLdapPassword() {
        return ldapPassword;
    }

    public void setLdapPassword(String ldapPassword) {
        this.ldapPassword = ldapPassword;
    }
    
}
