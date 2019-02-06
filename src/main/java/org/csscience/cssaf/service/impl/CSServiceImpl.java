/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.csscience.cssaf.configuration.AppConfig;
import org.csscience.cssaf.configuration.LocalConfiguration;
import org.csscience.cssaf.content.Album;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.csscience.cssaf.service.ZipcodeService;

/**
 *
 * @author john
 */
public class CSServiceImpl implements CSService {
    
    private AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

    @Autowired
    private StateService stateService;

    @Autowired
    private ZipcodeService zipService;

    private Properties prop = context.getBean(LocalConfiguration.class).localProperties();    
    private static CSService cSService = null;

    @Override
    public List<CSVLine> getCollectionData() {
        String collectionFile  = getPath("data.collection");
        List<CSVLine> collectionData = null;
        File f = new File(collectionFile);
        try {
            CSV csv = new CSV(f);
            collectionData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return collectionData;
    }

    @Override
    public List<CSVLine> getCSDData() {
        String csdFile  = getPath("data.csd");
        List<CSVLine> csdData = null;
        File f = new File(csdFile);
        try {
            CSV csv = new CSV(f);
            csdData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return csdData;
    }

    @Override
    public ZipcodeService getZipService() {
        if(zipService == null){
            zipService = (ZipcodeService) context.getBean(ZipcodeService.class);
        }
        return zipService;
    }

    @Override
    public StateService getStateService() {
        if(stateService == null){
            stateService = (StateService) context.getBean(StateService.class);
        }
        return stateService;
    }

    @Override
    public List<CSVLine> getImageData() {
        String photoDir  = getPath("data.photos");
        List<CSVLine> imageData = null;
        try {
            Album album = new Album(photoDir);
            imageData = album.imageLines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageData;
    }

    @Override
    public Properties getLocalProperties(){
        if(prop == null){
            prop = context.getBean(LocalConfiguration.class).localProperties();
        }
        return prop;
    }

    @Override
    public String getPath(String property){
        String path = "";        
        String cssafHome = prop.getProperty("cssaf.dir");
        String dateDir = prop.getProperty("date.dir");
        if(property.startsWith("data.")){
            String dir = prop.getProperty("data.dir");
            String dest = prop.getProperty(property);
            path = cssafHome + dir + dateDir + dest;
        } else if (property.startsWith("saf.")){
            String dir = prop.getProperty("saf.dir");
            String dest = prop.getProperty(property);
            path = cssafHome + dir + dateDir + dest;
        } else if(property.startsWith("library.")){
            String dest = prop.getProperty(property);
            path = cssafHome + dest;
        } else if(property.startsWith("date.")){
            String dest = prop.getProperty(property);
            String dir = prop.getProperty("saf.dir");
            path = cssafHome + dir + dateDir;
        }
        return path;
    }


    @Override
    public List<CSVLine> getCollectionTaxonomyData() {
        String collectionFile  = getPath("data.taxonomis");
        List<CSVLine> collectionData = null;
        File f = new File(collectionFile);
        try {
            CSV csv = new CSV(f);
            collectionData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return collectionData;        
    }

    @Override
    public List<CSVLine> getLinksData() {
        String csdFile  = getPath("data.links");
        List<CSVLine> csdData = null;
        File f = new File(csdFile);
        try {
            CSV csv = new CSV(f);
            csdData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return csdData;        
    }

    public static CSService getInstance(){
        if(cSService == null){
            cSService = new CSServiceImpl();
        }
        return cSService;
    }
}
