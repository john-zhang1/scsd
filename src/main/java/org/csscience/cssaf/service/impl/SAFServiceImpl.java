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

    private final String licensePath = "/usr/share/tomcat/webapps/scsd/WEB-INF/classes/collection/license.txt";
    private final String safNewPath = "/usr/share/tomcat/webapps/scsd/downloads/saf-new/";
    private final String existNewPath = "/usr/share/tomcat/webapps/scsd/downloads/saf-existing/";
    private final String photoDirectory = "/usr/share/tomcat/webapps/scsd/temp/images";
    
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
    public void createNewDataSAF() {
        CommonUtils.createDirectory(safNewPath);
        String defaultLicenseFile = licensePath;
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;

        List<CSVLine> newData = cSVService.getNewDataCSV();

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

}
