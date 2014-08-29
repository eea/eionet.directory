package eionet.directory.modules;

import eionet.directory.DirServiceException;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import java.util.Vector;

/**
 * Utility methods to parse LDAP search results.
 * Created by Enriko on 28.08.2014.
 */
public class DirectoryServiceUtils {

    /**
     * Parse uniqueMember attribute (role members) and extract uid properties.
     *
     * @param uniqueMemberAttribute enumeration of LDAP attributes.
     * @return Vector of uids.
     * @throws eionet.directory.DirServiceException when parsing LDAP attribute.
     */
    public static Vector<String> parseOccupants(Attribute uniqueMemberAttribute) throws DirServiceException {

        Vector<String> userNames = new Vector<String>();
        if (uniqueMemberAttribute == null) {
            return userNames;
        }

        try {
            String pattern1 = "uid=";
            NamingEnumeration nu = uniqueMemberAttribute.getAll();
            while (nu.hasMore()) {
                String s = (String) (nu.next());
                int pos1 = s.indexOf(pattern1);
                if (pos1 >= 0) {
                    int pos2 = s.indexOf(",", pos1 + pattern1.length());
                    if (pos2 < 0) {
                        s = s.substring(pos1 + pattern1.length()).trim();
                    } else {
                        s = s.substring(pos1 + pattern1.length(), pos2).trim();
                    }
                    if (s.length() > 0) {
                        userNames.add(s);
                    }
                }
            }
        } catch (NamingException ne) {
            throw new DirServiceException("Error getting user info" + ne.toString());
        } catch (NullPointerException nu) {
            throw new DirServiceException("Error getting user info" + nu.toString());
        }

        return userNames;
    }

    /**
     * Checks if any uniqueMember attribute (role members) contains user ID in uid property. The search is case insensitive.
     *
     * @param uniqueMemberAttribute enumeration of LDAP attributes.
     * @param userId                user unique id (uid)
     * @return true if user is a member of role.
     * @throws eionet.directory.DirServiceException when parsing LDAP attribute.
     */
    public static boolean occupantsContainsUserId(Attribute uniqueMemberAttribute, String userId) throws DirServiceException {

        if (uniqueMemberAttribute != null && userId != null) {
            Vector<String> occupants = DirectoryServiceUtils.parseOccupants(uniqueMemberAttribute);
            if (occupants != null) {
                for (String id : occupants) {
                    if (id.equalsIgnoreCase(userId)) {
                        return true;
                    }
                }
            }
        }
        return false;

    }

}

