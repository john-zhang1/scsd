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
import org.csscience.cssaf.content.Album;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSBatchService;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.CSVService;
import org.csscience.cssaf.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.csscience.cssaf.service.ZipcodeService;

@Service
public class CSVServiceImpl implements CSVService {

    @Autowired
    private StateService stateService;
    
    @Autowired
    private ZipcodeService zipService;
    
    private final String collectionCSV = "/usr/share/tomcat/webapps/scsd/WEB-INF/classes/collection/collection.csv";
    private final String csdFile = "/usr/share/tomcat/webapps/scsd/temp/csd.csv";
    private final String photoDir = "/usr/share/tomcat/webapps/scsd/temp/photos";

    protected List<CSVLine> collectionData = null;
    protected List<CSVLine> csdData = null;

    @Override
    public List<CSVLine> getCSDData() {
        if(csdData == null){
            File f = new File(csdFile);
            try {
                CSV csv = new CSV(f);
                csdData = csv.lines;
            } catch (Exception ex) {
                Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        return csdData;
    }

    @Override
    public List<CSVLine> getCollectionData() {
        if(collectionData == null){
            File f = new File(collectionCSV);
            try {
                CSV csv = new CSV(f);
                collectionData = csv.lines;
            } catch (Exception ex) {
                Logger.getLogger(CSBatchService.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }

        return collectionData;
    }

    @Override
    public List<CSVLine> getExitingDataCSV() {
        List<CSVLine> existingCSV = new ArrayList<>();
        List<CSVLine> csd = getCSDData();
        
        for(CSVLine sample : collectionData){
            String sampleID = sample.get("dwc.npdg.sampleid[]").get(0);
            if(sampleID != null && !"".equals(sampleID))
            {
                for(CSVLine cs : csd){
                    String csSampleID = cs.get("dwc.npdg.sampleid").get(0);
                    if(csSampleID != null && !"".equals(csSampleID) && sampleID.equals(csSampleID))
                    {
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
    public List<CSVLine> adjustedNewDataCSV() {
        List<CSVLine> newCSV = getNewDataCSV();
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
        CSVLine existingCSV = mergeColumns(existingSample);
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
    
    /** for citizen science collection csv form
     * @param sample */
    private CSVLine mergeColumns(CSVLine sample){
        CSVLine csv = new CSVLine(sample.getId());
        Set<String> keys = sample.keys();
        for(String key : keys)
        {
            String k = key.split("\\[")[0];
            csv.addAll(k, sample.get(key));
        }
        return csv;
    }

}
