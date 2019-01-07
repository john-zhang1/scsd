/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.csscience.cssaf.app.saf.SAF;
import org.csscience.cssaf.core.Constants;
import org.csscience.cssaf.csv.CSVLine;

/**
 *
 * @author john
 */
public class CommonUtils {
    public static void createDirectory(String directory){
        File dir = new File(directory);
        dir.mkdirs();
    }

    public static void copyFile(Path src, Path dest) {
        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Convert zip code as Decimal Format "00000"*/
    public static String convertZip(int zipcode) {
        DecimalFormat format = new DecimalFormat("00000");
        return format.format(zipcode);
    }

    public static List<String> getMetadataElements(String key){
        List<String> elements = new ArrayList<>(Arrays.asList(key.split("\\.")));
        return elements;
    }

    public static void writeFile(String name, String content)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(name);
            OutputStreamWriter osr = new OutputStreamWriter(fos, "UTF-8");
            try (PrintWriter out = new PrintWriter(osr)) {
                out.print(content);
            }
        } catch (IOException e)
        {
            Logger.getLogger(SAF.class.getName()).log(Level.SEVERE, null, e);
            System.exit(0);
        }
    }
    
    public static boolean ifDirectoryExists(File f){
        return Files.exists(f.toPath());
    }

    /** template of metadata_xml file */ 
    public static String getMetadataForm(CSVLine line, int type){
        StringBuilder sb = new StringBuilder();
        String metadataSchema = null;
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>");
        sb.append(System.getProperty("line.separator"));
        if(type == Constants.dc){
            sb.append("<dublin_core schema=\"dc\">");
            metadataSchema = "dc";
        }
        else if(type == Constants.dwc){
            sb.append("<dublin_core schema=\"dwc\">");
            metadataSchema = "dwc";
        }
        
        Map<String, ArrayList> items = line.getItems();
        Set<String> keySet = line.keys();
        for(String key : keySet)
        {
            List<String> elements = CommonUtils.getMetadataElements(key);
            if(elements.get(0).equals(metadataSchema) && elements.size() > 2 && items.get(key).size() > 0)
            {
                sb.append(System.getProperty("line.separator"));
                if(key.equals("dwc.npdg.homezip")){
                    sb.append("<dcvalue element=\"" + elements.get(1) + "\" "
                                + "qualifier=\"" + elements.get(2) + "\" language=\"\">" + CommonUtils.convertZip(Integer.parseInt((String)items.get(key).get(0))) + "</dcvalue>");
                }else{
                    sb.append("<dcvalue element=\"" + elements.get(1) + "\" "
                                + "qualifier=\"" + elements.get(2) + "\" language=\"\">" + ((String)items.get(key).get(0)).trim() + "</dcvalue>");
                }
            }
        }
        sb.append(System.getProperty("line.separator"));
        sb.append("</dublin_core>");
        return sb.toString();
    }


    /** template of license text */
    public static String getLicenseText(String licenseFile)
    {
        String license;
        InputStream is = null;
        InputStreamReader ir = null;
        BufferedReader br = null;
        try
        {
            is = new FileInputStream(licenseFile);
            ir = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(ir);
            String lineIn;
            license = "";
            while ((lineIn = br.readLine()) != null)
            {
                license = license + lineIn + '\n';
            }
        } catch (IOException e)
        {
            System.err.println("Failed to read default license.");
            throw new IllegalStateException("Failed to read default license.", e);
        } finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                } catch (IOException ioe)
                {
                }
            }
            if (ir != null)
            {
                try
                {
                    ir.close();
                }
                catch (IOException ioe)
                {
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                } catch (IOException ioe)
                {
                }
            }
        }
        return license;
    }

    /** template of contents file */
    public static String getContents(List<String> names){
        StringBuilder sb = new StringBuilder();
        String lineLicense = "license.txt	bundle:LICENSE";
        sb.append(lineLicense);
        sb.append(System.getProperty("line.separator"));
        
        for(String name : names){
            String line;
            line = name + "	bundle:ORIGINAL";
            sb.append(line);
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
        
    }
    
    /** template of handle file */
    public static String getHandle(CSVLine csv){
        String uriParts[] = null;
        if(csv.getItems().get("dc.identifier.uri") != null && csv.getItems().get("dc.identifier.uri").size() > 0){
            uriParts = ((String)csv.getItems().get("dc.identifier.uri").get(0)).split("\\/");
        }else if(csv.getItems().get("dc.identifier.uri[]") != null && csv.getItems().get("dc.identifier.uri[]").size() > 0){
            uriParts = ((String)csv.getItems().get("dc.identifier.uri[]").get(0)).split("\\/");
        }
        int urlLength = uriParts.length;
        String handle = null;
        if(urlLength > 4){
            handle = uriParts[urlLength - 2] + "/" + uriParts[urlLength - 1];
        } else
        {
            System.exit(1);
        }
        return handle;
    }
}
