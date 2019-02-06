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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.csscience.cssaf.content.Album;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.ValidateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValidateServiceImpl implements ValidateService {

    private static Logger log = Logger.getLogger(ZipServiceImpl.class);

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
        File[] listOfImages = directory.listFiles(filter);

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
        
        Set<String> keys = items.keySet();
        for(String key : keys)
        {
            if(items.get(key) != 2){
                errors.get("invalidPairs").add(key);
            }
        }

//        If folder contains images with error format, remove the folder
        if(errors.get("invalidNames").size() > 0 || errors.get("invalidPairs").size() > 0){
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            input.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Check with contents
        File f = file;
        CSV csv = null;
        try {
            csv = new CSV(f);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ValidateServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<CSVLine> lines = csv.lines;
        for(CSVLine line : lines)
        {
            items = line.getItems();
            // Check with zip format XXXXX
            try{
                if(!items.get("dwc.npdg.homezip").isEmpty()){
                    String zip = (String)items.get("dwc.npdg.homezip").get(0);
                    long zipCode = Long.parseLong(zip);
                    if(zipCode > 99999 || zipCode < 1){
                        errors.get("invalidZipFormat").add(line.getId());
                    }                    
                }else{
                    errors.get("invalidZipFormat").add(line.getId());
                }
            }catch(NumberFormatException e){
            }
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

}
