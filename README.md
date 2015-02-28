Eionet Directory module
=======================

Common and reusable component for Eionet LDAP operations.

Use eionetdir.properties to configure LDAP connection in your application.

Usage
-----
Include this into your maven pom.xml:
```xml
<dependency>
    <groupId>eionet</groupId>
    <artifactId>eionet-dir</artifactId>
    <version>3.0</version>
</dependency>
```

Configuration
-------------
The package can be configured via JNDI or a properties file. If a environment entry in JNDI is found then all required entries must be configured via JNDI. You configure the application through JNDI with the META-INF/context.xml of the web application using this package. In Tomcat all JNDI names will automatically be prefixed with `java:/comp/env`. Since it is a shared directory, configurations are in the `eionetdir/` sub-context. Example:

```xml
<Context>
    <Environment name="eionetdir/ldap.url" value="ldaps://ldap.eionet.europa.eu/"
                 type="java.lang.String" override="false"/>
</Context>
```

Alternatively copy `eionetdir.properties` from `src/test/resources` to your project's classpath and change the property values accordingly.
        
If you want to continue with property files, but specify with JNDI, what file to load the properties from, then you can add a context environment variable called `eionetdir/propertiesfile`
```xml
<Context>
    <Environment name="eionetdir/propertiesfile"
                 value="/var/local/my-app/eionetdir.properties"
                 type="java.lang.String" override="false"/>
</Context>
```

