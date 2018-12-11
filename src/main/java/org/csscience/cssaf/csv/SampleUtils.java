/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.StateService;
import org.csscience.cssaf.service.ZipService;
import org.csscience.cssaf.service.impl.CSServiceImpl;

/**
 *
 * @author john
 */
public class SampleUtils {
    protected static CSService cSService = CSServiceImpl.getInstance();
    ZipService zipService = cSService.getZipService();
    StateService stateService = cSService.getStateService();
    protected final List<CSVLine> collectionData = cSService.getCollectionData();
    protected final List<CSVLine> csdData = cSService.getCSDData();
    
//    protected StateDAO stateDAO = new StateDAOImpl();
//    protected ZipDAO zipDAO = new ZipDAOImpl();

    /** Get a csv contains exiting samples with metadata updated */
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
                        CSVLine updated = adjustedExistingDataCSV(sample, cs);
                        existingCSV.add(updated);
                    }
                }
            }
        }
        
        return existingCSV;
    }

    /** Get a csv contains new samples */
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
    
    /** Adjust new data form removing unnecessary columns and complete state and spatial data
     * @return newCSV */
    public List<CSVLine> adjustedNewDataCSV(){
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
    
    /** update existing sample data
     * @param existingSample
     * @param newSample
     * @return existingCSV */
    public CSVLine adjustedExistingDataCSV(CSVLine existingSample, CSVLine newSample){
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
    public CSVLine mergeColumns(CSVLine sample){
        CSVLine csv = new CSVLine(sample.getId());
        Set<String> keys = sample.keys();
        for(String key : keys)
        {
            String k = key.split("\\[")[0];
            csv.addAll(k, sample.get(key));
        }

        return csv;
    }

    protected final String convertMetadata(String head){
        String metadataString = null;
        head = head.trim();
        if((head != null) && !"".equals(head))
        {
            if(org.apache.commons.lang.StringUtils.contains(head, "Internal ID"))
                metadataString = "dwc.npdg.internalcode";
            else if(org.apache.commons.lang.StringUtils.contains(head, "Sample ID"))
                metadataString = "dwc.npdg.sampleid";
            else if(org.apache.commons.lang.StringUtils.contains(head, "Date Collected"))
                metadataString = "dwc.npdg.datecollected";
            else if(org.apache.commons.lang.StringUtils.contains(head, "City"))
                metadataString = "dwc.npdg.homecity";
            else if(org.apache.commons.lang.StringUtils.contains(head, "State"))
                metadataString = "dwc.npdg.homestate";
            else if(org.apache.commons.lang.StringUtils.contains(head, "Zip"))
                metadataString = "dwc.npdg.homezip";
            else if(org.apache.commons.lang.StringUtils.contains(head, "Screen Status"))
                metadataString = "screenstatus";
            else if(org.apache.commons.lang.StringUtils.contains(head, "# of isolates from RBM"))
                metadataString = "dwc.npdg.isolatesRBM";
            else if(org.apache.commons.lang.StringUtils.contains(head, "# of isolates from TV8"))
                metadataString = "dwc.npdg.isolatesTV8";
            else if(org.apache.commons.lang.StringUtils.contains(head, "Collection Detail"))
                metadataString = "dwc.npdg.detail";
            else if(org.apache.commons.lang.StringUtils.contains(head, "LIB"))
                metadataString = "lib";
            else if(org.apache.commons.lang.StringUtils.contains(head, "Photo"))
                metadataString = "dwc.npdg.imagestatus";
            else
                metadataString = head;
        }
        return metadataString;
    }   
}
