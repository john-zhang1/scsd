/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.content.Album;
import org.csscience.cssaf.core.Constants;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.CSVService;
import org.csscience.cssaf.service.SAFService;
import org.csscience.cssaf.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SAFServiceImpl implements SAFService {

    private final String baseDir = "/usr/share/tomcat/webapps/scsd";
    private final String dataDir = baseDir + File.separator + "data";
    private final String licensePath = baseDir + File.separator + "WEB-INF/classes/collection/license.txt";
    private final String safNewPath = "/usr/share/tomcat/webapps/scsd/downloads/saf-new/";
    private final String existNewPath = "/usr/share/tomcat/webapps/scsd/downloads/saf-existing/";
    private final String photoDirectory = dataDir + File.separator + "photos/";
    
    @Autowired
    private CSVService cSVService;

    @Override
    public void createExitingDataSAF() {
        CommonUtils.createDirectory(existNewPath);
        String mapfileName = existNewPath + "mapfile";
        StringBuilder builder = new StringBuilder();
        CommonUtils.writeFile(mapfileName, StringUtils.EMPTY);
        String defaultLicenseFile = licensePath;
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;

        List<CSVLine> existingData = cSVService.getExitingDataCSV();

        for(CSVLine csv : existingData)
        {
            String subDir = existNewPath + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);
            
            // Copy images
            List<String> imageNames = copyImages(csv, subDir);

            // Write Contents file
            String contents = CommonUtils.getContents(imageNames);
            String contentsName = subDir + "contents";
            CommonUtils.writeFile(contentsName, contents);
            
            // Write dublin_core.xml
            String xmlDC = CommonUtils.getMetadataForm(csv, Constants.dc);
            String dcFileName = subDir + "dublin_core.xml";
            CommonUtils.writeFile(dcFileName, xmlDC);

            // Write metadata_dwc.xml
            String xmlDWC = CommonUtils.getMetadataForm(csv, Constants.dwc);
            String dwcFileName = subDir + "metadata_dwc.xml";
            CommonUtils.writeFile(dwcFileName, xmlDWC);
            
            // Write handle file
            String handle = CommonUtils.getHandle(csv);
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


    @Override
    public void createExitingDataSAF(String sessionID) {
        String dir = dataDir + File.separator + sessionID + "/downloads/saf-existing";
        CommonUtils.createDirectory(dir);
        String mapfileName = dir + File.separator + "mapfile";
        StringBuilder builder = new StringBuilder();
        CommonUtils.writeFile(mapfileName, StringUtils.EMPTY);
        String defaultLicenseFile = licensePath;
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;

        List<CSVLine> existingData = cSVService.getExitingDataCSV(sessionID);

        for(CSVLine csv : existingData)
        {
            String subDir = dir + File.separator + Integer.toString(i);
            CommonUtils.createDirectory(subDir);
            
            // Copy images
            List<String> imageNames = copyImages(csv, subDir, sessionID, 0);

            // Write Contents file
            String contents = CommonUtils.getContents(imageNames);
            String contentsName = subDir + File.separator + "contents";
            CommonUtils.writeFile(contentsName, contents);
            
            // Write dublin_core.xml
            String xmlDC = CommonUtils.getMetadataForm(csv, Constants.dc);
            String dcFileName = subDir + File.separator + "dublin_core.xml";
            CommonUtils.writeFile(dcFileName, xmlDC);

            // Write metadata_dwc.xml
            String xmlDWC = CommonUtils.getMetadataForm(csv, Constants.dwc);
            String dwcFileName = subDir + File.separator + "metadata_dwc.xml";
            CommonUtils.writeFile(dwcFileName, xmlDWC);
            
            // Write handle file
            String handle = CommonUtils.getHandle(csv);
            String individualHandle = handle + System.getProperty("line.separator");
            String handleFileName = subDir + File.separator + "handle";
            CommonUtils.writeFile(handleFileName, individualHandle);

            // Mapfile content
            builder.append(Integer.toString(i)).append(" ").append(handle);
            builder.append(System.getProperty("line.separator"));
            
            // Write license.txt
            String licenseFileName = subDir + File.separator + "license.txt";
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

    @Override
    public void createNewDataSAF() {
        CommonUtils.createDirectory(safNewPath);
        String defaultLicenseFile = licensePath;
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;

        List<CSVLine> newData = cSVService.adjustedNewDataCSV();

        for(CSVLine csv : newData)
        {
            String subDir = safNewPath + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);

            // Copy images
            List<String> imageNames = copyImages(csv, subDir);

            // Write Contents file
            String contents = CommonUtils.getContents(imageNames);
            String contentsName = subDir + "contents";
            CommonUtils.writeFile(contentsName, contents);

            // Write dublin_core.xml
            String xmlDC = CommonUtils.getMetadataForm(csv, Constants.dc);
            String dcFileName = subDir + "dublin_core.xml";
            CommonUtils.writeFile(dcFileName, xmlDC);

            // Write metadata_dwc.xml
            String xmlDWC = CommonUtils.getMetadataForm(csv, Constants.dwc);            
            String dwcFileName = subDir + "metadata_dwc.xml";
            CommonUtils.writeFile(dwcFileName, xmlDWC);

            // Write license.txt
            String licenseFileName = subDir + "license.txt";
            CommonUtils.writeFile(licenseFileName, licenseText);
            
            i++;
        }
    }


    @Override
    public void createNewDataSAF(String sessionID) {
        String dir = dataDir + File.separator + sessionID + "/downloads/saf-new";
        CommonUtils.createDirectory(dir);
        String defaultLicenseFile = licensePath;
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;

        List<CSVLine> newData = cSVService.adjustedNewDataCSV(sessionID);

        for(CSVLine csv : newData)
        {
            String subDir = dir + File.separator + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);

            // Copy images
            List<String> imageNames = copyImages(csv, subDir, sessionID, 1);

            // Write Contents file
            String contents = CommonUtils.getContents(imageNames);
            String contentsName = subDir + "contents";
            CommonUtils.writeFile(contentsName, contents);

            // Write dublin_core.xml
            String xmlDC = CommonUtils.getMetadataForm(csv, Constants.dc);
            String dcFileName = subDir + "dublin_core.xml";
            CommonUtils.writeFile(dcFileName, xmlDC);

            // Write metadata_dwc.xml
            String xmlDWC = CommonUtils.getMetadataForm(csv, Constants.dwc);            
            String dwcFileName = subDir + "metadata_dwc.xml";
            CommonUtils.writeFile(dwcFileName, xmlDWC);

            // Write license.txt
            String licenseFileName = subDir + "license.txt";
            CommonUtils.writeFile(licenseFileName, licenseText);
            
            i++;
        }
    }

    @Override
    public List<String> copyImages(CSVLine csv, String dest) {
        String sampleID = csv.get("dwc.npdg.sampleid").get(0);
        List<String> imageNames = new ArrayList<>();
        List<CSVLine> imageData = getImageData();

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

    @Override
    public List<String> copyImages(CSVLine csv, String dest, String sessionID, int type) {
        String sampleID = csv.get("dwc.npdg.sampleid").get(0);
        List<String> imageNames = new ArrayList<>();
        List<CSVLine> imageData = getImageData(sessionID);
        String photoDir = dataDir + File.separator + sessionID + "/photos";

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
                        String fileLocation = photoDir + File.separator + title;
                        File f = new File(fileLocation);
                        if(f.exists())
                        {
                            Path p = f.toPath();
                            File fd = new File(dest + File.separator + title);
                            Path d = fd.toPath();
                            CommonUtils.copyFile(p, d);
                            if(!imageStatus.equals("Y")){
                                csv.clear("dwc.npdg.imagestatus");
                                csv.add("dwc.npdg.imagestatus", "Y");
                            }
                        }else
                        {
                            if(type == 1) {
                                if(imageStatus.equals("Y")){
                                    csv.clear("dwc.npdg.imagestatus");
                                    csv.add("dwc.npdg.imagestatus", "P");
                                }
                            }
                        }
                        imageNames.add(title);
                    }
                }else
                {
                    if(type == 1) {
                        if(imageStatus.equals("Y")){
                            csv.clear("dwc.npdg.imagestatus");
                            csv.add("dwc.npdg.imagestatus", "P");
                        }                        
                    }
                }
            }
        }
        return imageNames;
    }

    @Override
    public List<CSVLine> getImageData() {
        List<CSVLine> imageData = null;
        try {
            Album album = new Album(photoDirectory);
            imageData = album.imageLines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageData;
    }

    @Override
    public List<CSVLine> getImageData(String sessionID) {
        String photoDir = dataDir + File.separator + sessionID + "/photos";
        List<CSVLine> imageData = null;
        try {
            Album album = new Album(photoDir);
            imageData = album.imageLines;
        } catch (Exception ex) {
            Logger.getLogger(CSService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imageData;
    }

}
