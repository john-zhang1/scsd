/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.csscience.cssaf.core.Constants;
import org.csscience.cssaf.csv.CSVLine;
import org.csscience.cssaf.service.CSVService;
import org.csscience.cssaf.service.TaxonomyService;
import org.csscience.cssaf.service.ValidateService;
import org.csscience.cssaf.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxonomyServiceImpl implements TaxonomyService {

    /** log4j logger */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TaxonomyServiceImpl.class);

    public final String directory = "/usr/share/tomcat/webapps/scsd/downloads/saf-taxonomy/";
    

    @Autowired
    private CSVService cSVService;

    @Autowired
    private ValidateService validateService;

    @Override
    public boolean createTaxonomySAF() {
        Map<String, ArrayList> errors = validateService.validateTaxonomy();
        boolean success = false;

        if(errors.get("invalidInternalCode").isEmpty()){
            List<CSVLine> collectionData = cSVService.getCollectionTaxonomyData();
            List<CSVLine> LinksData = cSVService.getLinksData();
            CommonUtils.createDirectory(directory);

            int i = 1;
            for(CSVLine link : LinksData)
            {
                String subDir = directory + Integer.toString(i) + "/";
                String internalCode = null;
                if(link.getItems() != null){
                    if(link.getItems().get("dwc.npdg.internalcode") != null && link.getItems().get("dwc.npdg.internalcode").size() > 0){
                        internalCode = (String)link.getItems().get("dwc.npdg.internalcode").get(0);
                    }
                    String wikiLink = (String)link.getItems().get("Link").get(0);
                    if(wikiLink.trim()==null || wikiLink.trim().equals("")){
                        link.remove("Link");
                        link.add("Link", "Uncultured fungus");
                    }
                }

                String handle = null;
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
                }
                i++;
            }
            if(i == LinksData.size() + 1){
                success = true;
            }
        }
        return success;
    }
    
}
