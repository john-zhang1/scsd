/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.CSVService;
import org.csscience.cssaf.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.csscience.cssaf.service.ZipcodeService;
import org.csscience.cssaf.utils.CommonUtils;

@Service
public class CSVServiceImpl implements CSVService {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StateServiceImpl.class);

    private List<CSVLine> collectionTaxonomyData = null;
    

    @Autowired
    private StateService stateService;

    @Autowired
    private ZipcodeService zipService;

    private final String baseDir = "/usr/share/tomcat/webapps/scsd";
    private final String dataDir = baseDir + File.separator + "data";
    private final String collectionCSV = "/usr/share/tomcat/webapps/scsd/data/collection.csv";
    private final String collectionTaxonomyCSV = "/usr/share/tomcat/webapps/scsd/data/taxCollection.csv";
    private final String collectionLinkCSV = "/usr/share/tomcat/webapps/scsd/data/link.csv";
    private final String csdFile = "/usr/share/tomcat/webapps/scsd/data/csd.csv";

    @Override
    public List<CSVLine> getCSDData() {
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
    public List<CSVLine> getCSDData(String sessionID) {
        String file = dataDir + File.separator + sessionID + "/csd.csv";
        List<CSVLine> csdData = null;
        File f = new File(file);
        try {
            CSV csv = new CSV(f);
            csdData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }            

        return csdData;        
    }

    @Override
    public List<CSVLine> getCollectionData() {
        List<CSVLine> collectionData = null;
        File f = new File(collectionCSV);
        try {
            CSV csv = new CSV(f);
            collectionData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSVService.class.getName()).log(Level.SEVERE, null, ex);
        }            

        return collectionData;
    }

    @Override
    public List<CSVLine> getCollectionData(String sessionID) {
        String collectionFile = dataDir + File.separator + sessionID + "/collection.csv";
        List<CSVLine> collectionData = null;
        File f = new File(collectionFile);
        try {
            CSV csv = new CSV(f);
            collectionData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSVService.class.getName()).log(Level.SEVERE, null, ex);
        }            

        return collectionData;
    }

    @Override
    public List<CSVLine> getExitingDataCSV() {
        List<CSVLine> existingCSV = new ArrayList<>();
        List<CSVLine> csd = getCSDData();
        List<CSVLine> collectionData = getCollectionData();

        for(CSVLine cs : csd){
            String csSampleID = cs.get("dwc.npdg.sampleid").get(0);
            System.out.println(csSampleID);
            if(csSampleID != null && !"".equals(csSampleID)){
                for(CSVLine sample : collectionData){
                    String sampleID = sample.get("dwc.npdg.sampleid[]").get(0);
                    System.out.println(sampleID);
                    if(sampleID.equals(csSampleID)){
                        CSVLine updated = adjustedExistingDataCSV(sample, cs);
                        existingCSV.add(updated);
                    }
                }
            }
        }
        
        return existingCSV;
    }

    @Override
    public List<CSVLine> getExitingDataCSV(String sessionID) {
        List<CSVLine> existingCSV = new ArrayList<>();
        List<CSVLine> csd = getCSDData(sessionID);
        List<CSVLine> collectionData = getCollectionData(sessionID);
        
        for(CSVLine cs : csd){
            String csSampleID = cs.get("dwc.npdg.sampleid").get(0);
            System.out.println(csSampleID);
            if(csSampleID != null && !"".equals(csSampleID)){
                for(CSVLine sample : collectionData){
                    String sampleID = sample.get("dwc.npdg.sampleid[]").get(0);
                    System.out.println(sampleID);
                    if(sampleID.equals(csSampleID)){
                        CSVLine updated = adjustedExistingDataCSV(sample, cs);
                        existingCSV.add(updated);
                    }
                }
            }
        }
        
        return existingCSV;
    }

    @Override
    public List<CSVLine> getNewDataCSV() {
        List<CSVLine> csd = getCSDData();
        List<CSVLine> newCSV = new ArrayList<>();
        List<CSVLine> collectionData = getCollectionData();

        for(CSVLine line : csd){
            boolean collision = false;
            String csSampleID = line.get("dwc.npdg.sampleid").get(0);
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
                    newCSV.add(line);                    
                }
            }
        }
        return newCSV;
    }

    @Override
    public List<CSVLine> getNewDataCSV(String sessionID) {
        List<CSVLine> csd = getCSDData(sessionID);
        List<CSVLine> newCSV = new ArrayList<>();
        List<CSVLine> collectionData = getCollectionData(sessionID);

        for(CSVLine line : csd){
            boolean collision = false;
            String csSampleID = line.get("dwc.npdg.sampleid").get(0);
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
                    newCSV.add(line);                    
                }
            }
        }
        return newCSV;
    }

    @Override
    public List<CSVLine> adjustedNewDataCSV() {
        List<CSVLine> newCSV = null;
        newCSV = getNewDataCSV();
        String keyState = "dwc.npdg.homestate";
        String keyZip = "dwc.npdg.homezip";
        String keySpatial = "dwc.npdg.spatial";
        String keyLib = "lib"; // ignore
        String keyScreenStatus = "screenstatus"; // ignore
        String fullState;
        for(CSVLine line : newCSV)
        {
            String st = line.get(keyState).get(0);
            fullState = stateService.combinedName(st);
            line.clear(keyState);
            line.add(keyState, fullState);
            String zipcode = (String)line.get(keyZip).get(0);
            // Add dwc.npdg.spatial
            List<String> latlon = new ArrayList<>();
            String s = zipService.getLatLon(zipcode);
            latlon.add(s);
            line.addAll(keySpatial, latlon);
            
            line.remove(keyLib);
            line.remove(keyScreenStatus);
        }
        return newCSV;
    }

    @Override
    public List<CSVLine> adjustedNewDataCSV(String sessionID) {
        List<CSVLine> newCSV = null;
        newCSV = getNewDataCSV(sessionID);
        String keyState = "dwc.npdg.homestate";
        String keyZip = "dwc.npdg.homezip";
        String keySpatial = "dwc.npdg.spatial";
        String keyLib = "lib"; // ignore
        String keyScreenStatus = "screenstatus"; // ignore
        String fullState;
        for(CSVLine line : newCSV)
        {
            String st = line.get(keyState).get(0);
            fullState = stateService.combinedName(st);
            line.clear(keyState);
            line.add(keyState, fullState);
            String zipcode = (String)line.get(keyZip).get(0);
            // Add dwc.npdg.spatial
            List<String> latlon = new ArrayList<>();
            String s = zipService.getLatLon(zipcode);
            latlon.add(s);
            line.addAll(keySpatial, latlon);
            
            line.remove(keyLib);
            line.remove(keyScreenStatus);
        }
        return newCSV;
    }

    @Override
    public CSVLine adjustedExistingDataCSV(CSVLine existingSample, CSVLine newSample) {
        CSVLine existingCSV = CommonUtils.mergeColumns(existingSample);
        Set<String> keysExisting = existingCSV.keys();
        Set<String> keysNew = newSample.keys();
        
        for(String keyExisting : keysExisting)
        {
            if(!keyExisting.equals("dwc.npdg.homestate") )
            {
                for(String keyNew : keysNew)
                {
                    if(keyExisting.equals(keyNew))
                    {
                        if(newSample.get(keyNew) != null){
                            existingCSV.clear(keyExisting);
                            existingCSV.addAll(keyExisting, newSample.get(keyNew));
                        }
                    }
                }
            }
        }
        return existingCSV;
    }

    @Override
    public List<CSVLine> getCollectionTaxonomyData() {
        if(collectionTaxonomyData==null){
            File f = new File(collectionTaxonomyCSV);
            try {
                CSV csv = new CSV(f);
                collectionTaxonomyData = csv.lines;
            } catch (Exception ex) {
                Logger.getLogger(CSVServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        return collectionTaxonomyData;
    }

    @Override
    public List<CSVLine> getCollectionTaxonomyData(String sessionID) {
        String taxCSV = dataDir + File.separator + sessionID + "/taxCollection.csv";
        if (collectionTaxonomyData == null) {
            File f = new File(taxCSV);
            try {
                CSV csv = new CSV(f);
                collectionTaxonomyData = csv.lines;
            } catch (Exception ex) {
                Logger.getLogger(CSVServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return collectionTaxonomyData;
    }

    @Override
    public List<CSVLine> getLinksData() {
        List<CSVLine> csdData = null;
        File f = new File(collectionLinkCSV);
        try {
            CSV csv = new CSV(f);
            csdData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSVServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return csdData;        
    }

    @Override
    public List<CSVLine> getLinksData(String sessionID) {
        List<CSVLine> linkData = null;
        String linkCSV = dataDir + File.separator + sessionID + "/link.csv";

        File f = new File(linkCSV);
        try {
            CSV csv = new CSV(f);
            linkData = csv.lines;
        } catch (Exception ex) {
            Logger.getLogger(CSVServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return linkData;        

    }

}