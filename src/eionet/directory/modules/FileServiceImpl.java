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

import eionet.directory.FileServiceIF;
import eionet.directory.DirServiceException;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * File services implementation.
 *
 * @author  Rando Valt
 * @version 1.1
 */
public class FileServiceImpl implements FileServiceIF {
   
  public static final String PROP_FILE = "eionetdir";
   
  private ResourceBundle props;
  private String appRoot;
  //private LogServiceIF log;
   
/** Creates new FileServiceImpl */
  public FileServiceImpl() throws DirServiceException {
     try {
        props = ResourceBundle.getBundle(PROP_FILE);
     } catch (MissingResourceException mre) {
       throw new DirServiceException("Properties file " + PROP_FILE + ".properties not found");
     }
     //log = WFServices.getLogService();
     // initialize application root property
     appRoot = "";
     /*
     try {
       appRoot = props.getString(WF_APP_ROOT_DIR);
     } catch (MissingResourceException mre) {/do nothing, app root was not given/ }
     */
     
  }

/**
 *
 */
  public String getStringProperty(String propName) throws DirServiceException {
    try {
       return props.getString(propName);
    } catch (MissingResourceException mre) {
       throw new DirServiceException("Property value for key " + propName + " not found");
    }
  }
    
/**
 *
 */
  public boolean getBooleanProperty(String propName) throws DirServiceException {
    try {
       String s = props.getString(propName);
       return Boolean.valueOf(s).booleanValue();
    } catch (MissingResourceException mre) {
       throw new DirServiceException("Property value for key " + propName + " not found");
    }
  }

/**
 *
 */
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

/**
 *
 */
  public String getAppDirectory(String dirName) throws DirServiceException {
    try {
      String dir = props.getString(dirName);
      String fileSep = System.getProperty("file.separator");
      
      // find out, if we have relative path - then append it to
      // the root directory
      if (dir.length() == 0)
        dir = appRoot;
      else  {
        if ( fileSep.equals("/") )  { // unix
          if ( !dir.startsWith("/") ) // relative path
            dir = appRoot + "/" + dir;
        }
        else  { // M$
          if (dir.length() < 2 || dir.charAt(1) != ':' ) // relative path
            dir = appRoot + "\\" + dir;
        }
      }
      
      return dir;
    } catch (MissingResourceException mre) {
       throw new DirServiceException("Directory for key " + dirName + " not found");
    }
  }

  
}


