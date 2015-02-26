package eionet.directory.modules;

import eionet.directory.DirServiceException;
import org.junit.Test;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import java.util.Vector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test DirectoryServiceUtils methods.
 * Created by Enriko on 28.08.2014.
 */
public class DirectoryServiceUtilsTest {

    @Test
    public void parseOccupants() throws DirServiceException {
        Attribute attribute = getAttribute();
        Vector occupants = DirectoryServiceUtils.parseOccupants(attribute);

        assertTrue(occupants.contains("user1"));
        assertTrue(occupants.contains("user2"));
        assertTrue(occupants.contains("user3"));
        assertEquals(3, occupants.size());
    }

    @Test
    public void occupantsContainUserId() throws DirServiceException {

        Attribute attribute = getAttribute();
        assertTrue(DirectoryServiceUtils.occupantsContainsUserId(attribute, "USER1"));
        assertTrue(DirectoryServiceUtils.occupantsContainsUserId(attribute, "user1"));
        assertFalse(DirectoryServiceUtils.occupantsContainsUserId(attribute, "user999"));
        assertFalse(DirectoryServiceUtils.occupantsContainsUserId(attribute, null));
    }

    private static Attribute getAttribute() {
        Attribute attribute = new BasicAttribute("");
        attribute.add("uid=user1,ou=Users,o=EIONET,l=Europe");
        attribute.add("uid=user2,ou=Users,o=EIONET,l=Europe");
        attribute.add("uid=user3,ou=Users,o=EIONET,l=Europe");
        return attribute;
    }
}
