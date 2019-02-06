/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.configuration.AppConfig;
import org.csscience.cssaf.configuration.LocalConfiguration;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSBatchService;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.StateService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ServletContextAware;
import org.csscience.cssaf.service.ZipcodeService;

@Service
public class CSBatchServiceImpl extends AbstractApplicationContext implements CSBatchService {

    /** log4j logger */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StateServiceImpl.class);

    @Autowired
    private AbstractApplicationContext context;
    
    private final String collectionCSV = "/usr/share/tomcat/webapps/scsd/WEB-INF/classes/collection/collection.csv";

    @Autowired
    private StateService stateService;

    @Autowired
    private ZipcodeService zipService;

    private static CSBatchService cSBatchService = null;

    private final List<CSVLine> collectionData = getCollectionData();
    
    private final List<CSVLine> csdData = getCollectionData();
    
    @Override
    public List<CSVLine> getCollectionData() {
        String collectionFile  = collectionCSV;
        List<CSVLine> collectionData = null;
        File f = new File(collectionFile);
        try {
            CSV csv = new CSV(f);
            collectionData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSBatchService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return collectionData;
    }

    @Override
    public List<CSVLine> getCollectionTaxonomyData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CSVLine> getCSDData(String csdFile) {
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

    /** Get a csv contains exiting samples with metadata updated */
    @Override
    public List<CSVLine> getExitingDataCSV(){
        List<CSVLine> existingCSV = new ArrayList<>();
        
        for(CSVLine sample : collectionData){
            String sampleID = sample.get("dwc.npdg.sampleid[]").get(0);
            if(sampleID != null && !"".equals(sampleID))
            {
                for(CSVLine cs : csdData){
                    String csSampleID = cs.get("dwc.npdg.sampleid").get(0);
                    if(csSampleID != null && !"".equals(csSampleID) && sampleID.equals(csSampleID))
                    {
//                        CSVLine updated = adjustedExistingDataCSV(sample, cs);
//                        existingCSV.add(updated);
                    }
                }
            }
        }
        
        return existingCSV;
    }

    /** Get a csv contains new samples */
    @Override
    public List<CSVLine> getNewDataCSV(){
        List<CSVLine> newCSV = new ArrayList<>();
        
        for(CSVLine csd : csdData){
            boolean collision = false;
            String csSampleID = csd.get("dwc.npdg.sampleid").get(0);
            if(csSampleID != null && !"".equals(csSampleID))
            {
                for(CSVLine sample : collectionData){
                    String sampleID = sample.get("dwc.npdg.sampleid[]").get(0);
                    if(StringUtils.equals(csSampleID, sampleID))
                    {
                        collision = true;
                        break;
                    }
                }
                if(!collision)
                {
                    newCSV.add(csd);                    
                }
            }
        }
        return newCSV;
    }
    @Override
    public List<CSVLine> getLinksData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CSVLine> getImageData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Properties getLocalProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPath(String property) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void refreshBeanFactory() throws BeansException, IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void closeBeanFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
