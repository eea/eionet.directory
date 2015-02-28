package eionet.directory.modules;

import eionet.directory.DirServiceException;
import org.junit.Test;

import java.util.Hashtable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test DirectoryServiceUtilsTest methods.
 */
public class DirectoryService25ImplTest extends JNDIAware {

    @Test
    public void getPerson() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        // If my name is not in LDAP then I don't care if the test fails.
        Hashtable<String, String> person = ds.getPerson("roug");
        assertEquals("Søren Roug", person.get("FULLNAME"));
    }

    @Test
    public void getPersonWithLdaps() throws Exception {
        addToTomcatContext("propertiesfile", "target/test-classes/test-ldaps.properties");

        DirectoryService25Impl ds = new DirectoryService25Impl();
        Hashtable<String, String> person = ds.getPerson("roug");
        assertEquals("Søren Roug", person.get("FULLNAME"));
    }
}
