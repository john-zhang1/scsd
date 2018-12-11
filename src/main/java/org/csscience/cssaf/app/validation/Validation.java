/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.csscience.cssaf.content.Album;
import org.csscience.cssaf.csv.CSV;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.impl.CSServiceImpl;

/**
 *
 * @author john
 */
public class Validation {

    private static CSService servicesFactory = CSServiceImpl.getInstance();

    public Validation(){

    }

    public boolean validatePhotos() throws Exception{
        boolean foundIssue = false;
        List<String> invalidNames = new ArrayList<>();
        List<String> invalidPairs = new ArrayList<>();
        Map<String, Integer> items = new HashMap<>();

        String path = servicesFactory.getPath("data.photos");
        File directory = new File(path);
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
                invalidNames.add(imgName);
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
        if(invalidNames.size() > 0){
            foundIssue = true;
        }
        
        Set<String> keys = items.keySet();
        for(String key : keys)
        {
            if(items.get(key) != 2){
                invalidPairs.add(key);
            }
        }
        if(invalidPairs.size() > 0){
            foundIssue = true;
        }

        if(foundIssue){
            System.err.println("Failed to match required format: ");
            System.out.println(invalidNames.toString());
            System.err.println("Samples don't contain exact two images: ");
            System.out.println(invalidPairs.toString());
            System.exit(1);
        }

        return foundIssue;
    }
    
    public boolean isNamePatternMatched(String name){
        boolean isMatched;
        String pattern = "^[^0]\\d+_[R|T]\\s2.jpg$";
        isMatched = name.matches(pattern);
        return isMatched;
    }

    public boolean validateCSV() throws Exception{
        boolean foundIssue = false;
        List<String> invalidSamples = new ArrayList<>();
        List<String> invalidHeadings = new ArrayList<>();
        String filePath = servicesFactory.getPath("data.csd");
        Map<String, ArrayList> items;

        // Check with the headings format
        File fHeadings = new File(filePath);
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(fHeadings),"UTF-8"));
        // Read the heading line
        String head = input.readLine();
        String[] headingElements = head.split(",");
        for(String s : headingElements)
        {
            s = s.trim();
            if(!(s.contains("Internal ID") || s.contains("Sample ID") || s.contains("Date Collected") || s.contains("LIB")
                     || s.contains("City") || s.contains("State") || s.contains("Zip") || s.contains("Screen Status") || s.contains("Photo")
                     || s.contains("# of isolates from RBM") || s.contains("# of isolates from TV8") || s.contains("Collection Detail"))){
                invalidHeadings.add(s);
            }
        }
        if (input != null)
        {
            input.close();
        }

        // Check with contents
        File f = new File(filePath);
        CSV csv = new CSV(f);
        List<CSVLine> lines = csv.lines;
        for(CSVLine line : lines)
        {
            items = line.getItems();
            // Check with zip format
            try{
                String zip = (String)items.get("dwc.npdg.homezip").get(0);
                int zipCode = Integer.parseInt(zip);
                if(zipCode > 99999 || zipCode < 1){
                    invalidSamples.add(line.getId());
                }
            }catch(NumberFormatException e){
                System.err.println("Headings contain wrong columns: dwc.npdg.homezip");
                throw(e);
            }
        }

        if(invalidSamples.size() > 0 || invalidHeadings.size() > 0){
            foundIssue = true;
        }
        
        if(foundIssue){
            System.err.println("Headings contain wrong columns: ");
            System.out.println(invalidHeadings.toString());
            System.err.println("Samples contain wrong information: ");
            System.out.println(invalidSamples.toString());
            System.exit(1);
        }

        return foundIssue;
    }    
}
