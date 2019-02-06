/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.configuration;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 *
 * @author john
 */
@Configuration
@ComponentScan("org.csscience.cssaf.configuration")
@PropertySource(value={"classpath:local.properties"})
public class LocalConfiguration {
    @Autowired
    private Environment environment;

    public Properties localProperties() {
        Properties properties = new Properties();
        properties.put("cssaf.dir", environment.getRequiredProperty("cssaf.dir"));
        properties.put("library.dir", environment.getRequiredProperty("library.dir"));
        properties.put("data.dir", environment.getRequiredProperty("data.dir"));
        properties.put("data.collection", environment.getRequiredProperty("data.collection"));
        properties.put("data.csd", environment.getRequiredProperty("data.csd"));
        properties.put("data.photos", environment.getRequiredProperty("data.photos"));
        properties.put("data.points", environment.getRequiredProperty("data.points"));
        properties.put("data.taxonomis", environment.getRequiredProperty("data.taxonomis"));
        properties.put("data.links", environment.getRequiredProperty("data.links"));
        properties.put("saf.dir", environment.getRequiredProperty("saf.dir"));
        properties.put("saf.new", environment.getRequiredProperty("saf.new"));
        properties.put("saf.existing", environment.getRequiredProperty("saf.existing"));
        properties.put("saf.taxonomy", environment.getRequiredProperty("saf.taxonomy"));
        properties.put("saf.pointjson", environment.getRequiredProperty("saf.pointjson"));
        properties.put("date.dir", environment.getRequiredProperty("date.dir"));
        return properties;
    }
}
