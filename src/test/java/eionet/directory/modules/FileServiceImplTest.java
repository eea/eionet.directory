package eionet.directory.modules;

import eionet.directory.DirServiceException;
import eionet.directory.DynamicProperties;
import eionet.directory.DynamicPropertiesLoader;
import eionet.directory.FileService;
import java.util.Hashtable;

import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test FileServiceImpl methods.
 */
public class FileServiceImplTest extends JNDIAware {

    @Test
    public void standardInitialisation() throws Exception {
        FileServiceImpl fileService = new FileServiceImpl();
    }

    @Test
    public void withIncompleteTomcatContext() throws Exception {
        addToTomcatContext("admin", "true");
        FileServiceImpl fileService = new FileServiceImpl();
        assertNull(fileService.getOptionalStringProperty(FileService.LDAP_URL));
    }

    @Test
    public void loadSpecifiedFile() throws Exception {
        addToTomcatContext("propertiesfile", "target/test-classes/test-ldaps.properties");
        FileServiceImpl fileService = new FileServiceImpl();
        assertEquals("ldaps://ldap.eionet.europa.eu/", fileService.getStringProperty(FileService.LDAP_URL));
    }
    
    @Test
    public void testDynamicProperties() throws DirServiceException {
        //Create object that normally is injected through spring bean
        DynamicProperties dynamicProperties = new DynamicProperties();
        dynamicProperties.setLdapPassword("pass");
        dynamicProperties.setLdapPrincipal("principal");
        dynamicProperties.setLdapUrl("ldapUrl");
        DynamicPropertiesLoader.setDynamicProperties(dynamicProperties);
        
        //Assert that spring injected values are read correctly
        FileServiceImpl fileService = new FileServiceImpl();
        assertEquals("pass", fileService.getStringProperty(FileService.LDAP_PASSWORD));
        assertEquals("principal", fileService.getStringProperty(FileService.LDAP_PRINCIPAL));
        assertEquals("ldapUrl", fileService.getStringProperty(FileService.LDAP_URL));
    }
}
