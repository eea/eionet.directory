package eionet.directory.modules;

import eionet.directory.DirServiceException;
import eionet.directory.FileServiceIF;

import java.util.Vector;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
        InitialContext ic = new InitialContext();
        ic.bind(aclContextLocation + "admin", "true");
        FileServiceImpl fileService = new FileServiceImpl();
        // Clean up or it will affect other tests.
        ic.unbind(aclContextLocation + "admin");
    }

    @Test
    public void loadSpecifiedFile() throws Exception {
        InitialContext ic = new InitialContext();
        ic.bind(aclContextLocation + "propertiesfile", "target/test-classes/test-ldaps.properties");
        FileServiceImpl fileService = new FileServiceImpl();
        assertEquals("ldaps://ldap.eionet.europa.eu/", fileService.getStringProperty(FileServiceIF.LDAP_URL));
        ic.unbind(aclContextLocation + "propertiesfile");
    }
}
