package eionet.directory.modules;

import eionet.directory.DirServiceException;
import org.junit.Test;

import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test DirectoryServiceUtilsTest methods.
 */
public class DirectoryService25ImplTest {

    @Test
    public void getPerson() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        Hashtable<String, String> person = ds.getPerson("bulanmir");
    }

}
