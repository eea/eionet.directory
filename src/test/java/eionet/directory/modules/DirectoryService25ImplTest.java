package eionet.directory.modules;

import eionet.directory.DirServiceException;
import eionet.directory.DirectoryServiceIF;
import eionet.directory.dto.OrganisationDTO;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.junit.Test;

import java.util.Hashtable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test DirectoryServiceUtilsTest methods.
 */
public class DirectoryService25ImplTest extends JNDIAware {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getPerson() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        // If my name is not in LDAP then I don't care if the test fails.
        Hashtable<String, String> person = ds.getPerson("roug");
        assertEquals("Søren Roug", person.get("FULLNAME"));
    }

    @Test
    public void getOrganisationDTO() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        // If EEA is not in LDAP then I don't care if the test fails.
        OrganisationDTO org = ds.getOrganisationDTO("eu_eea");
        assertEquals("European Environment Agency", org.getName());
        assertEquals("eu_eea", org.getOrgId());
    }

    @Test
    public void getOrgDTONotExisting() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        OrganisationDTO org = ds.getOrganisationDTO("au_nothere");
        assertEquals(null, org.getName());
    }

    @Test
    public void getOrganisation() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        Hashtable<String, Object> org = ds.getOrganisation("eu_eea");
        assertEquals("European Environment Agency", org.get(DirectoryServiceIF.ORG_NAME_ATTR));
        assertEquals("eu_eea", org.get(DirectoryServiceIF.ORG_ID_ATTR));
    }

    @Test
    public void getOrgNotExisting() throws DirServiceException {
        DirectoryService25Impl ds = new DirectoryService25Impl();
        exception.expect(DirServiceException.class);
        exception.expectMessage("No such organisation in directory: au_nothere");
        Hashtable<String, Object> org = ds.getOrganisation("au_nothere");
    }

    @Test
    public void getPersonWithLdaps() throws Exception {
        addToTomcatContext("propertiesfile", "target/test-classes/test-ldaps.properties");

        DirectoryService25Impl ds = new DirectoryService25Impl();
        Hashtable<String, String> person = ds.getPerson("roug");
        assertEquals("Søren Roug", person.get("FULLNAME"));
    }

    @Test
    public void getPersonWithBadServer() throws Exception {
        addToTomcatContext("propertiesfile", "target/test-classes/test-badldap.properties");

        DirectoryService25Impl ds = new DirectoryService25Impl();
        exception.expect(SecurityException.class);
        exception.expectMessage("Cannot connect to : ldap://localhost:40389/");
        Hashtable<String, String> person = ds.getPerson("roug");
    }
}
