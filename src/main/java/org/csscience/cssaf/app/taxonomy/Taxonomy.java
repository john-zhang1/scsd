/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.taxonomy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.core.Constants;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.impl.CSServiceImpl;
import org.csscience.cssaf.utils.CommonUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author john
 */
public class Taxonomy {
    
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Taxonomy.class);

    private static CSService servicesFactory = CSServiceImpl.getInstance();

    public Taxonomy() {
    }

    public void createTaxonomySAF()
    {
        List<CSVLine> collectionData = servicesFactory.getCollectionTaxonomyData();
        List<CSVLine> LinksData = servicesFactory.getLinksData();
        String directory = servicesFactory.getPath("saf.taxonomy");
        CommonUtils.createDirectory(directory);
        
        List<String> invalidInternalCode = new ArrayList<>();

        int i = 1;
        for(CSVLine link : LinksData)
        {
            String subDir = directory + Integer.toString(i) + "/";
            String internalCode = null;
            if(link.getItems() != null){
                if(link.getItems().get("dwc.npdg.internalcode") != null && link.getItems().get("dwc.npdg.internalcode").size() > 0){
                    internalCode = (String)link.getItems().get("dwc.npdg.internalcode").get(0);
                }else{
                    log.info("Links file: Line number " + i + " does not have Internal ID");
                    System.out.println("Links file: Line number " + i + " does not have Internal ID"); 
                }
                String wikiLink = (String)link.getItems().get("Link").get(0);
                if(wikiLink.trim()==null || wikiLink.trim().equals("")){
                    link.remove("Link");
                    link.add("Link", "Uncultured fungus");
                }
            }

            String handle = null;
            int j = 1;
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
                        
                        break;
                    }
                }
                j++;
            }
            if(handle != null)
            {
                CommonUtils.createDirectory(subDir);
                
                String link_taxonomy = (String)link.getItems().get("Link").get(0);
                CSVLine csv = new CSVLine(Integer.toString(i));
                // Make sure using https:// instaed of http://
                if(handle.contains("http:")){
                    String replace = handle.replace("http:", "https:");
                    handle = replace;
                }
                csv.add("dc.identifier.uri", handle);
                csv.add("dc.relation.wiki", link_taxonomy);
                String xmlDC = CommonUtils.getMetadataForm(csv, Constants.dc);
                String dcFileName = subDir + "dublin_core.xml";
                CommonUtils.writeFile(dcFileName, xmlDC);
            }else{
                invalidInternalCode.add(internalCode);
            }
            i++;
        }
        if(invalidInternalCode.size() >0){
            System.out.print(invalidInternalCode.toString());
        }
    }
}
