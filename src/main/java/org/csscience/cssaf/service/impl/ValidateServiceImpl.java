/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.csscience.cssaf.content.Album;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSVService;
import org.csscience.cssaf.service.ValidateService;
import org.csscience.cssaf.service.ZipcodeService;
import org.csscience.cssaf.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValidateServiceImpl implements ValidateService {

    private static Logger log = Logger.getLogger(ZipServiceImpl.class);

    private Map<String, ArrayList> rowsWithErrors = new HashMap<>();

    private Map<String, ArrayList> rowsWithTaxErrors = new HashMap<>();

    @Autowired
    private ZipcodeService zipcodeService;
    
    @Autowired
    private CSVService cSVService;
    
    @Override
    public Map<String, ArrayList> validatePhotoNameFormat(String sourceDir) {

        Map<String, ArrayList> errors = new HashMap<>();
        errors.put("invalidNames", new ArrayList<>());
        errors.put("invalidPairs", new ArrayList<>());
        Map<String, Integer> items = new HashMap<>();

        File directory = new File(sourceDir);
        FilenameFilter filter;
        filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".jpg");
            }
        };

        FilenameFilter unfilter;
        unfilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return !lowercaseName.endsWith(".jpg");
            }
        };

        File[] listOfImages = directory.listFiles(filter);
        File[] listOfRejectImages = directory.listFiles(unfilter);

        for(File f : listOfImages)
        {
            String imgName = f.getName();
            boolean isBadFormat = isNamePatternMatched(imgName);
            if(!isBadFormat){
                errors.get("invalidNames").add(imgName);
            }

            String key = Album.getImageKey(imgName);
            if(key != null && !"".equals(key)){
                if(items.get(key) == null){
                    items.put(key, 1);
                }else{
                    int count = items.get(key);
                    items.put(key, ++count);
                }
            }
        }
        
        for(File f : listOfRejectImages) {
            String imgName = f.getName();
            errors.get("invalidNames").add(imgName);
        }

        Set<String> keys = items.keySet();
        for(String key : keys)
        {
            if(items.get(key) != 2){
                errors.get("invalidPairs").add(key);
            }
        }

        return errors;
    }

    @Override
    public Map<String, ArrayList> validateCSV(File file) {
        Map<String, ArrayList> items;
        Map<String, ArrayList> errors = new HashMap<>();
        errors.put("invalidHeadings", new ArrayList<>());
        errors.put("invalidZipFormat", new ArrayList<>());
        errors.put("zipAddressNotMached", new ArrayList<>());

        // Check with the headings format
        File fHeadings = file;
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(fHeadings),"UTF-8"));
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Read the heading line
        String head = null;
        try {
            head = input.readLine();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] headingElements = head.split(",");
        for(String s : headingElements)
        {
            s = s.trim();
            if(!(s.contains("Internal ID") || s.contains("Sample ID") || s.contains("Date Collected") || s.contains("LIB")
                     || s.contains("City") || s.contains("State") || s.contains("Zip") || s.contains("Screen Status") || s.contains("Photo")
                     || s.contains("# of isolates from RBM") || s.contains("# of isolates from TV8") || s.contains("Collection Detail"))){
                errors.get("invalidHeadings").add(s);
            }
        }
        if(errors.get("invalidHeadings").size() > 0) {
            addAll("head", Arrays.asList(headingElements));
        }

        try {
            input.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Check with contents
        File f = file;
        CSV csv = null;
        List<CSVLine> lines = null;
        try {
            csv = new CSV(f);
            lines = csv.lines;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }


        for(CSVLine line : lines)
        {
            items = line.getItems();
            boolean hasError = false;

            if(!items.get("dwc.npdg.homezip").isEmpty()){
                String zip = (String)items.get("dwc.npdg.homezip").get(0);
                if(isNumeric(zip)) {
                    long zipCode = Long.parseLong(zip);
                    if(zipCode < 99999 && zipCode > 1) {
                        String state = (String)items.get("dwc.npdg.homestate").get(0);
                        String city = (String)items.get("dwc.npdg.homecity").get(0);
                        if(!isZipPlaceMatched(zip, state, city)) {
                            errors.get("zipAddressNotMached").add(line.getId());
                            hasError = true;
                        }
                    } else {
                        errors.get("invalidZipFormat").add(line.getId());
                        hasError = true;
                    }
                } else {
                    errors.get("invalidZipFormat").add(line.getId());
                    hasError = true;
                }
            }else{
                errors.get("invalidZipFormat").add(line.getId());
                hasError = true;
            }

            if(hasError) {
                for(String key : items.keySet()) {
                    addAll(line.getId(), items.get(key));
                }
            }
        }

        // If csv file contains errors, remove the file
        if(errors.get("invalidHeadings").size()>0 || errors.get("invalidZipFormat").size()>0 || errors.get("zipAddressNotMached").size()>0){
//            file.delete();
            add("csvErrorFlag", "1");
        }

        return errors;
    }

    @Override
    public Map<String, ArrayList> validateZipcode(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//
//        List<String> invalidZipcodes = new ArrayList<>();
//        List<CSVLine> csdData = servicesFactory.getCSDData();
//        ZipService zipService = servicesFactory.getZipService();
//        
//        for(CSVLine csv : csdData)
//        {
//            String zipcode = null;
//            try{
//                zipcode = (String) csv.get("dwc.npdg.homezip").get(0);
//            }catch(Exception e){
//                System.err.println("No zipcode is provided." + e);
//                System.out.println(csv.getId() + " does not have zipcode.");
//            }
//            
//            String latLon = zipService.getLatLon(zipcode);
//            if(latLon == null || latLon.equals("")){
//                invalidZipcodes.add(zipcode);
//            }
//        }
//        
//        if(invalidZipcodes.size() > 0){
//            System.err.println("Invalid zipcodes: ");
//            System.out.println(invalidZipcodes.toString());
//            System.exit(1);            
//        }
    }

    private boolean isNamePatternMatched(String name){
        boolean isMatched;
        String pattern = "^[^0]\\d+_[R|T]\\s2.jpg$";
        isMatched = name.matches(pattern);
        return isMatched;
    }

    private boolean isZipPlaceMatched(String homeZip, String homeState, String homeCity){
        boolean isMatched = false;
        Zip zip = zipcodeService.findByZip(homeZip);
        String state = null;
        String city = null;
        
        if(zip == null){
            return false;
        }else{
            state = zip.getShortState();
            city = zip.getCity();
            if(zip != null || StringUtils.equals(homeState, state) || StringUtils.equals(homeCity, city)){
                isMatched = true;
            }            
        }

        return isMatched;
    }

    @Override
    public Map<String, ArrayList> validateTaxonomy() {

        Map<String, ArrayList> errors = new HashMap<>();
        errors.put("invalidInternalCode", new ArrayList<>());

        List<CSVLine> collectionData = cSVService.getCollectionTaxonomyData();
        List<CSVLine> LinksData = cSVService.getLinksData();

        for(CSVLine link : LinksData)
        {
            String internalCode = null;
            if(link.getItems() != null){
                if(link.getItems().get("dwc.npdg.internalcode") != null && link.getItems().get("dwc.npdg.internalcode").size() > 0){
                    internalCode = (String)link.getItems().get("dwc.npdg.internalcode").get(0);
                }else{
                     errors.get("invalidInternalCode").add(internalCode);
                }
            }

            String handle = null;
            boolean isInternalCodeFound = false;
            for(CSVLine line : collectionData)
            {
                String internalID = null;
                if(line.getItems() != null)
                {
                    if(line.getItems().get("dwc.npdg.internalcode") != null && line.getItems().get("dwc.npdg.internalcode").size() > 0){
                        internalID = (String)line.getItems().get("dwc.npdg.internalcode").get(0);
                    }else if(line.getItems().get("dwc.npdg.internalcode[]") != null && line.getItems().get("dwc.npdg.internalcode[]").size() > 0){
                        internalID = (String)line.getItems().get("dwc.npdg.internalcode[]").get(0);
                    }
                }
                if(internalCode != null && internalID != null)
                {
                    if(StringUtils.equals(internalCode, internalID)){
                        if(line.getItems() != null)
                        {
                            if(line.getItems().get("dc.identifier.uri") != null && line.getItems().get("dc.identifier.uri").size() > 0){
                                handle = (String)line.getItems().get("dc.identifier.uri").get(0);
                            }else if(line.getItems().get("dc.identifier.uri[]") != null && line.getItems().get("dc.identifier.uri[]").size() > 0){
                                handle = (String)line.getItems().get("dc.identifier.uri[]").get(0);
                            }
                        }
                        isInternalCodeFound = true;
                        break;
                    }
                }
            }
            if(handle == null || !isInternalCodeFound){
                errors.get("invalidInternalCode").add(internalCode);
            }
        }
        return errors;
    }

    @Override
    public Map<String, ArrayList> validateTaxonomy(String sessionID) {

        Map<String, ArrayList> items;
        List<CSVLine> collectionData = cSVService.getCollectionTaxonomyData(sessionID);
        List<CSVLine> linksData = cSVService.getLinksData(sessionID);
        int counter = 1;
        for(CSVLine link : linksData)
        {
            items = link.getItems();
            String internalCode = null;
            if(items != null){
                // if internal code is null
                if(items.get("dwc.npdg.internalcode") != null && items.get("dwc.npdg.internalcode").size() > 0){
                    internalCode = (String)items.get("dwc.npdg.internalcode").get(0);
                }else{
                    addTaxAll(counter, getTaxRow(items));
                    counter++;
                }
                // if link is null -- Uncultured fungus
                String linkUrl = (String)items.get("Link").get(0);
                if(linkUrl == null) {
                    linkUrl = "";
                } else {
                    linkUrl = linkUrl.trim();
                }
                if("".equals(linkUrl)) {
//                    addTaxAll(counter, getTaxRow(items));
//                    counter++;
                } else {
                    // if link is invalid
                    if(!CommonUtils.validateUrl((String)items.get("Link").get(0))) {
                        addTaxAll(counter, getTaxRow(items));
                        counter++;
                    }
                }
            }

            // if internal code doesn't exist
            boolean isInternalCodeFound = false;
            for(CSVLine line : collectionData)
            {
                String internalID = null;
                if(line.getItems() != null)
                {
                    if(line.getItems().get("dwc.npdg.internalcode") != null && line.getItems().get("dwc.npdg.internalcode").size() > 0){
                        internalID = (String)line.getItems().get("dwc.npdg.internalcode").get(0);
                    }else if(line.getItems().get("dwc.npdg.internalcode[]") != null && line.getItems().get("dwc.npdg.internalcode[]").size() > 0){
                        internalID = (String)line.getItems().get("dwc.npdg.internalcode[]").get(0);
                    }
                }
                if(internalCode != null && internalID != null)
                {
                    if(StringUtils.equals(internalCode, internalID)){
                        isInternalCodeFound = true;
                        break;
                    }
                }
            }
            if(!isInternalCodeFound && internalCode != null){
                addTaxAll(counter, getTaxRow(items));
                counter++;
            }
        }
        return rowsWithTaxErrors;
    }

    @Override
    public Map<String, ArrayList> getRowsWithErrors(File file) {
        rowsWithErrors = new HashMap<>();
        validateCSV(file);
        return rowsWithErrors;
    }

    @Override
    public Map<String, ArrayList> getRowsWithTaxErrors(String sessionID) {
        rowsWithTaxErrors = new HashMap<>();
        validateTaxonomy(sessionID);
        return rowsWithTaxErrors;
    }

    private void add(String key, String value){
        if(rowsWithErrors.get(key) == null){
            rowsWithErrors.put(key, new ArrayList<>());
        }
        if(rowsWithErrors.get(key) != null){
            rowsWithErrors.get(key).add(value);
        }
    }

    private void addAll(String key, List<String> value)
    {
        if (rowsWithErrors.get(key) == null) {
            rowsWithErrors.put(key, new ArrayList<>());
        }
        if (value != null) {
            rowsWithErrors.get(key).addAll(value);
        }
    }

    private void addTax(int counter, String value){
        String key = Integer.toString(counter);
        if(rowsWithTaxErrors.get(key) == null){
            rowsWithTaxErrors.put(key, new ArrayList<>());
        }
        if(rowsWithTaxErrors.get(key) != null){
            rowsWithTaxErrors.get(key).add(value);
        }
    }

    private void addTaxAll(int counter, List<String> value)
    {
        String key = Integer.toString(counter);
        if (rowsWithTaxErrors.get(key) == null) {
            rowsWithTaxErrors.put(key, new ArrayList<>());
        }
        if (value != null) {
            rowsWithTaxErrors.get(key).addAll(value);
        }
    }

    private List<String> getTaxRow(Map<String, ArrayList> items) {
        List<String> ls = new ArrayList<>();

        for (List l : items.values()) {
            if (l.size() > 0) {
                ls.add((String) l.get(0));
            } else {
                ls.add("");
            }
        }
        Collections.reverse(ls);
        return ls;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long d = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
