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
 * Copyright (c) 2000-2015 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Rando Valt (TietoEnator)
 * Contributor: Søren Roug
 */

package eionet.directory.modules;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Binding;
import javax.naming.NamingException;

import eionet.directory.DirServiceException;
import eionet.directory.DynamicPropertiesLoader;
import eionet.directory.FileService;

/**
 * File services implementation.
 *
 * @author  Søren Roug
 * @version 2.0
 */
public class FileServiceImpl implements FileService {

    /** Tomcat puts its stuff under java:comp/env. */
    private static final String TOMCAT_CONTEXT = "java:comp/env/";

    /** Properties file prefix. */
    private static final String PROP_FILE = "eionetdir";

    /** Properties resource. */
    private Hashtable<Object,Object> props;

    /**
     * Creates new FileServiceImpl.
     * @throws DirServiceException if properties are not found.
     */
    public FileServiceImpl() throws DirServiceException {
        loadProperties(PROP_FILE);
    }

    @Override
    public String getStringProperty(String propName) throws DirServiceException {
        Object o = props.get(propName);
        if (o == null) {
            throw new DirServiceException("Property value for key " + propName + " not found");
        }
        return (String) o;
    }

    @Override
    public String getOptionalStringProperty(String propName) {
        return (String) props.get(propName);
    }

/*
    //TODO: Check if the value is already Boolean or don't use JNDI directly.
    @Override
    public boolean getBooleanProperty(String propName) throws DirServiceException {
        try {
            String s = (String) props.get(propName);
            return Boolean.valueOf(s).booleanValue();
        } catch (MissingResourceException mre) {
            throw new DirServiceException("Property value for key " + propName + " not found");
        }
    }

    //TODO: Check if the value is already Integer or don't use JNDI directly.
    @Override
    public int getIntProperty(String propName) throws DirServiceException {
        try {
            String s = (String) props.get(propName);
            return Integer.parseInt(s);
        } catch (MissingResourceException mre) {
            throw new DirServiceException("Property value for key " + propName + " not found");
        } catch (NumberFormatException nfe) {
            throw new DirServiceException("Invalid value for integer property " + propName);
        }
    }
*/
    /**
     * Load properties from JNDI context or properties file as fall-back.
     *
     * @return Hashtable of the properties
     * @throws DirServiceException if no file found.
     */
    //@SuppressWarnings({ "unchecked", "rawtypes" })
    private void loadProperties(String propFile) throws DirServiceException {

        if (props == null) {
            props = new Hashtable<Object,Object>();

            try {
                Context initContext = new InitialContext();
                if (initContext != null) {
                    // Load from JNDI. Tomcat puts its stuff under java:comp/env:
                    for (Enumeration<Binding> e = initContext.listBindings(TOMCAT_CONTEXT + propFile); e.hasMoreElements();) {
                        Binding binding = e.nextElement();
                        props.put(binding.getName(), binding.getObject());
                    }
                }
            } catch (NamingException mre) {
                //throw new DirServiceException("JNDI not configured properly");
            }

            // Load from properties file
            if (props.size() == 0 || props.containsKey("propertiesfile")) {
                try {
                    Properties fileProps = new Properties();
                    InputStream inStream = null;

                    if (props.containsKey("propertiesfile")) {
                        try {
                            inStream = new FileInputStream((String) props.get("propertiesfile"));
                        } catch (Exception e) {
                            throw new DirServiceException("Properties file not found");
                        }
                    } else {
                        inStream = getClass().getResourceAsStream("/" + propFile + ".properties");
                        if (inStream == null) {
                            throw new DirServiceException("Properties file " + propFile + ".properties is not found in the classpath");
                        }
                    }
                    fileProps.load(inStream);
                    inStream.close();
                    props.putAll(fileProps);
                } catch (IOException mre) {
                    throw new DirServiceException("Properties file " + propFile + ".properties is not readable");
                }
            }
        }
        
        //If DynamicProperties object contains property values, these values overwrite anything obtained through file props or JNDI resource.
        if (DynamicPropertiesLoader.dynamicProperties != null) {
            Hashtable dynamicProps = DynamicPropertiesLoader.getDynamicProperties();
            for (Object key : dynamicProps.keySet()) {
                props.put(key, dynamicProps.get(key));
            }
        }
        
    }
}
