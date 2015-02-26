package eionet.directory.modules;

import eionet.directory.DirServiceException;
import org.junit.Test;
import org.junit.After;

import java.util.Vector;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test FileServiceImpl methods.
 */
public class FileServiceImplTest {

    static String aclContextLocation = "java:comp/env/eionetdir/";
    static boolean isSetupCore = false;

    public static void setUpCore() throws Exception {
        if (isSetupCore) {
            return;
        }
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        ic.createSubcontext("java:");
        ic.createSubcontext("java:comp");
        ic.createSubcontext("java:comp/env");
        ic.createSubcontext("java:comp/env/jdbc");
        ic.createSubcontext("java:comp/env/eionetdir");
        isSetupCore = true;
    }

    @After
    public void cleanUp() {
        try {
            InitialContext ic = new InitialContext();
            ic.destroySubcontext("java:comp/env/eionetdir");
        } catch (NamingException e) {
        }
    }

    @Test
    public void standardInitialisation() throws Exception {
        FileServiceImpl fileService = new FileServiceImpl();
    }

    @Test
    public void withTomcatContext() throws Exception {
        setUpCore();
        InitialContext ic = new InitialContext();
        ic.bind(aclContextLocation + "admin", "true");
        FileServiceImpl fileService = new FileServiceImpl();
    }

}
