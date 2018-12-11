/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.saf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.core.Constants;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.csv.SampleUtils;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.impl.CSServiceImpl;
import org.csscience.cssaf.utils.CommonUtils;

/**
 *
 * @author john
 */
public class SAF {
    protected List<CSVLine> newData;
    protected List<CSVLine> existingData;
    protected List<CSVLine> imageData;
    private static CSService servicesFactory = CSServiceImpl.getInstance();
    private static SampleUtils Samples = new SampleUtils();
    
    public SAF(){
        this.newData = Samples.adjustedNewDataCSV();
        this.existingData = Samples.getExitingDataCSV();
        this.imageData = servicesFactory.getImageData();
    }
    
    public void createExitingDataSAF()
    {
        String directory = servicesFactory.getPath("saf.existing");
        CommonUtils.createDirectory(directory);
        String photoDirectory = servicesFactory.getPath("data.photos");
        String mapfileName = directory + "mapfile";
        StringBuilder builder = new StringBuilder();
        CommonUtils.writeFile(mapfileName, StringUtils.EMPTY);
        String defaultLicenseFile = servicesFactory.getPath("library.dir") + "license.txt";
        String licenseText = getLicenseText(defaultLicenseFile);
        int i = 1;

        for(CSVLine csv : existingData)
        {
            String subDir = directory + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);
            
            // Copy images
            List<String> imageNames = copyImages(csv, photoDirectory, subDir);

            // Write Contents file
            String contents = getContents(imageNames);
            String contentsName = subDir + "contents";
            CommonUtils.writeFile(contentsName, contents);
            
            // Write dublin_core.xml
            String xmlDC = getMetadataForm(csv, Constants.dc);
            String dcFileName = subDir + "dublin_core.xml";
            CommonUtils.writeFile(dcFileName, xmlDC);

            // Write metadata_dwc.xml
            String xmlDWC = getMetadataForm(csv, Constants.dwc);
            String dwcFileName = subDir + "metadata_dwc.xml";
            CommonUtils.writeFile(dwcFileName, xmlDWC);
            
            // Write handle file
            String handle = getHandle(csv);
            String individualHandle = handle + System.getProperty("line.separator");
            String handleFileName = subDir + "handle";
            CommonUtils.writeFile(handleFileName, individualHandle);

            // Mapfile content
            builder.append(Integer.toString(i)).append(" ").append(handle);
            builder.append(System.getProperty("line.separator"));
            
            // Write license.txt
            String licenseFileName = subDir + "license.txt";
            CommonUtils.writeFile(licenseFileName, licenseText);

            i++;
        }
        
        // Remove last new line "\n"
        int last = builder.lastIndexOf("\n");
        if (last >= 0) { 
            builder.delete(last, builder.length()); 
        }
        // Write mapfile                
        CommonUtils.writeFile(mapfileName, builder.toString());
     }
    
    public void createNewDataSAF()
    {
        String directory = servicesFactory.getPath("saf.new");
        CommonUtils.createDirectory(directory);
        String photoDirectory = servicesFactory.getPath("data.photos");
        String defaultLicenseFile = servicesFactory.getPath("library.dir") + "license.txt";
        String licenseText = getLicenseText(defaultLicenseFile);
        int i = 1;
        
        for(CSVLine csv : newData)
        {
            String subDir = directory + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);

            // Copy images
            List<String> imageNames = copyImages(csv, photoDirectory, subDir);

            // Write Contents file
            String contents = getContents(imageNames);
            String contentsName = subDir + "contents";
            CommonUtils.writeFile(contentsName, contents);

            // Write dublin_core.xml
            String xmlDC = getMetadataForm(csv, Constants.dc);
            String dcFileName = subDir + "dublin_core.xml";
            CommonUtils.writeFile(dcFileName, xmlDC);

            // Write metadata_dwc.xml
            String xmlDWC = getMetadataForm(csv, Constants.dwc);            
            String dwcFileName = subDir + "metadata_dwc.xml";
            CommonUtils.writeFile(dwcFileName, xmlDWC);

            // Write license.txt
            String licenseFileName = subDir + "license.txt";
            CommonUtils.writeFile(licenseFileName, licenseText);
            
            i++;
        }
    }

    /** template of metadata_xml file */ 
    private String getMetadataForm(CSVLine line, int type){
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
                                + "qualifier=\"" + elements.get(2) + "\" language=\"\">" + items.get(key).get(0) + "</dcvalue>");
                }
            }
        }
        sb.append(System.getProperty("line.separator"));
        sb.append("</dublin_core>");
        return sb.toString();
    }

    /** template of contents file */
    private String getContents(List<String> names){
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
    private String getHandle(CSVLine csv){
        String uriParts[] = ((String)csv.getItems().get("dc.identifier.uri").get(0)).split("\\/");
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
    
    /** get images for each sample */
    private List<String> copyImages(CSVLine csv, String photoDirectory, String dest){
        String sampleID = csv.get("dwc.npdg.sampleid").get(0);
        List<String> imageNames = new ArrayList<>();
        for(CSVLine image : imageData)
        {
            String id = image.getId();
            if(StringUtils.equals(sampleID, id))
            {
                List<String> list = image.get(id);
                // check to see the image status
                String imageStatus = csv.get("dwc.npdg.imagestatus").get(0);
                if(list.size() == 2)
                {
                    for(String title : list)
                    {
                        String fileLocation = photoDirectory + title;
                        File f = new File(fileLocation);
                        if(f.exists())
                        {
                            Path p = f.toPath();
                            File fd = new File(dest + title);
                            Path d = fd.toPath();
                            CommonUtils.copyFile(p, d);
                            if(!imageStatus.equals("Y")){
                                csv.clear("dwc.npdg.imagestatus");
                                csv.add("dwc.npdg.imagestatus", "Y");
                            }
                        }else
                        {
                            if(imageStatus.equals("Y")){
                                csv.clear("dwc.npdg.imagestatus");
                                csv.add("dwc.npdg.imagestatus", "P");
                            }
                        }
                        imageNames.add(title);
                    }
                }else
                {
                    if(imageStatus.equals("Y")){
                        csv.clear("dwc.npdg.imagestatus");
                        csv.add("dwc.npdg.imagestatus", "P");
                    }
                }
            }
        }
        return imageNames;
    }

    /** template of license text */
    private String getLicenseText(String licenseFile)
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
}
