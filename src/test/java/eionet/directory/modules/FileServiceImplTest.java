package eionet.directory.modules;

import eionet.directory.DirServiceException;
import eionet.directory.FileService;

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
}
