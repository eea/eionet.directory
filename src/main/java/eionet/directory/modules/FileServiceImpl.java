/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "UIT".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (c) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Rando Valt (TietoEnator)
 */

package eionet.directory.modules;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import eionet.directory.DirServiceException;
import eionet.directory.FileServiceIF;

/**
 * File services implementation.
 *
 * @author  Rando Valt
 * @version 1.1
 */
public class FileServiceImpl implements FileServiceIF {

    /** Properties file prefix. */
    public static final String PROP_FILE = "eionetdir";

    /** Properties resource. */
    private ResourceBundle props;

    /**
     * Creates new FileServiceImpl.
     * @throws DirServiceException if eionetdir.properties file is not found.
     */
    public FileServiceImpl() throws DirServiceException {
        try {
            props = ResourceBundle.getBundle(PROP_FILE);
        } catch (MissingResourceException mre) {
            throw new DirServiceException("Properties file " + PROP_FILE + ".properties not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStringProperty(String propName) throws DirServiceException {
        try {
            return props.getString(propName);
        } catch (MissingResourceException mre) {
            throw new DirServiceException("Property value for key " + propName + " not found");
        }
    }

    /**
     * {@inheritDoc}
     */
   @Override
   public String getOptionalStringProperty(String propName){
       try {
           return props.getString(propName);
       } catch (MissingResourceException mre) {
           return "";
       }
   }

   /**
    * {@inheritDoc}
    */
    @Override
    public boolean getBooleanProperty(String propName) throws DirServiceException {
        try {
            String s = props.getString(propName);
            return Boolean.valueOf(s).booleanValue();
        } catch (MissingResourceException mre) {
            throw new DirServiceException("Property value for key " + propName + " not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntProperty(String propName) throws DirServiceException {
        try {
            String s = props.getString(propName);
            return Integer.parseInt(s);
        } catch (MissingResourceException mre) {
            throw new DirServiceException("Property value for key " + propName + " not found");
        } catch (NumberFormatException nfe) {
            throw new DirServiceException("Invalid value for integer property " + propName);
        }
    }
}
