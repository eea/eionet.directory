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
The package can be configured via JNDI, properties file or dependency injection. If a environment entry in JNDI is found then all required entries must be configured via JNDI. You configure the application through JNDI with the META-INF/context.xml of the web application using this package. In Tomcat all JNDI names will automatically be prefixed with `java:/comp/env`. Since it is a shared directory, configurations are in the `eionetdir/` sub-context. Example:

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

Some environmetal properties can be configured by injecting an object of class eionet.directory.DynamicProperties.

For instance, if you use spring configuration you could define:

```xml
<bean id="directoryProperties" class="eionet.directory.DynamicProperties">
    <property name="ldapPrincipal" value="${env.ldap.principal}" />
    <property name="ldapPassword" value="${env.ldap.password}" />
    <property name="ldapUrl" value="${env.ldap.url}"/>
</bean>
    
<bean id="directoryPropertiesLoader" class="eionet.directory.DynamicPropertiesLoader">
    <property name="dynamicProperties" ref="directoryProperties"/>
</bean>
```

Alternatively you can use the corresponding static method of the DynamicPropertiesLoader class for setting the DynamicProperties object.

Note that all values defined through the DynamicPropertiesLoader will overwrite all other property values.

Obsolete properties
-------------------
The following properties in eionetdir.properties were needed in earlier versions of the package:
```
circa.vcirca
circa.url.public
circa.url.members
circa.role.function.prefix
circa.role.function.suffix
circa.version
ldap.user.context
ldap.domain
```

