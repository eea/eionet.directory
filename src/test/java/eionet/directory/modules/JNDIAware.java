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


/**
 * Support functions to set up JNDI.
 */
public class JNDIAware {

    static String aclContextLocation = "java:comp/env/eionetdir/";
    private boolean isSetupCore = false;

    /**
     * Clean up. This does not unlink the bound objects.
     */
    @After
    public void cleanUp() throws Exception {
        if (!isSetupCore) {
            return;
        }
        InitialContext ic = new InitialContext();
        ic.destroySubcontext("java:comp/env/eionetdir");
        ic.destroySubcontext("java:comp/env");
        ic.destroySubcontext("java:comp");
        ic.destroySubcontext("java:");
        isSetupCore = false;
    }

    @Before
    public void setUpCore() throws Exception {
        if (isSetupCore) {
            return;
        }
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        InitialContext ic = new InitialContext();
        ic.createSubcontext("java:");
        ic.createSubcontext("java:comp");
        ic.createSubcontext("java:comp/env");
        ic.createSubcontext("java:comp/env/eionetdir");
        isSetupCore = true;
    }

}
