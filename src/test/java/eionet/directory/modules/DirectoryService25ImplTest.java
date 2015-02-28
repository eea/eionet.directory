package eionet.directory.modules;

import eionet.directory.DirServiceException;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
