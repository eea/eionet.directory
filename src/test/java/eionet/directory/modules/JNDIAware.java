package eionet.directory.modules;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * Support functions to set up JNDI.
 */
public class JNDIAware {

    static String aclContextLocation = "java:comp/env/eionetdir/";
    private boolean isSetupCore = false;
    protected InitialContext context;
    private ArrayList<String> addedProps = new ArrayList<String>();

    /**
     * Clean up. This does not unlink the bound objects.
     */
    @After
    public void cleanUp() throws Exception {
        if (!isSetupCore) {
            return;
        }
        for (String name : addedProps) {
            context.unbind(aclContextLocation + name);
        }
        addedProps.clear();
        context.destroySubcontext("java:comp/env/eionetdir");
        context.destroySubcontext("java:comp/env");
        context.destroySubcontext("java:comp");
        context.destroySubcontext("java:");
        isSetupCore = false;
    }

    @Before
    public void setUpCore() throws Exception {
        if (isSetupCore) {
            return;
        }
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        context = new InitialContext();
        context.createSubcontext("java:");
        context.createSubcontext("java:comp");
        context.createSubcontext("java:comp/env");
        context.createSubcontext("java:comp/env/eionetdir");
        isSetupCore = true;
    }

    /**
     * Add a property to Tomcat's context.
     */
    void addToTomcatContext(String name, String value) throws Exception {
        context.bind(aclContextLocation + name,  value);
        addedProps.add(name);
    }

}
