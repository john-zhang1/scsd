/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.content;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.csscience.cssaf.csv.CSVLine;

/**
 *
 * @author john
 */
public class Album {
    protected static String imageDirectory;
    public List<CSVLine> imageLines;
    protected static Map<String, ArrayList> items;
    
    public Album(String imageDirectory) {
        Album.imageDirectory = imageDirectory;
        items = new TreeMap<>();
        imageLines = new ArrayList<>();
        getImageList();
    }
    
    protected void add(String key, String value){
        if(items.get(key) == null){
            items.put(key, new ArrayList<>());
        }
        if(items.get(key) != null){
            items.get(key).add(value);
        }
    }

    public static String getImageKey(String imageTitle){
        String part = null;
        if(imageTitle != null && !"".equals(imageTitle)){
            part = imageTitle.split("_")[0];
        }
        return part;
    }

    protected void getImageList(){
        if(imageLines.isEmpty())
        {
            File directory = new File(imageDirectory);
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
                CSVLine csvLine = null;
                String key = getImageKey(f.getName());
                if(key != null && !"".equals(key))
                {
                    add(key, f.getName());
                }
            }

            for(String k : items.keySet())
            {
                List<String> l = items.get(k);
                Collections.sort(l);
                CSVLine csvLine = new CSVLine(k);
                csvLine.addAll(k, l);
                imageLines.add(csvLine);
            }
        }
    }    
}
