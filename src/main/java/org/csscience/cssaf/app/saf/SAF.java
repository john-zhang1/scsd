/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.saf;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;

        for(CSVLine csv : existingData)
        {
            String subDir = directory + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);
            
            // Copy images
            List<String> imageNames = copyImages(csv, photoDirectory, subDir);

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
    
    public void createNewDataSAF()
    {
        String directory = servicesFactory.getPath("saf.new");
        CommonUtils.createDirectory(directory);
        String photoDirectory = servicesFactory.getPath("data.photos");
        String defaultLicenseFile = servicesFactory.getPath("library.dir") + "license.txt";
        String licenseText = CommonUtils.getLicenseText(defaultLicenseFile);
        int i = 1;
        
        for(CSVLine csv : newData)
        {
            String subDir = directory + Integer.toString(i) + "/";
            CommonUtils.createDirectory(subDir);

            // Copy images
            List<String> imageNames = copyImages(csv, photoDirectory, subDir);

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
}
