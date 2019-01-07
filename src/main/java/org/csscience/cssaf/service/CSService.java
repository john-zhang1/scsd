/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.util.List;
import java.util.Properties;
import org.csscience.cssaf.csv.CSVLine;

/**
 *
 * @author john
 */
public interface CSService {

    public List<CSVLine> getCollectionData();

    public List<CSVLine> getCollectionTaxonomyData();

    public List<CSVLine> getCSDData();

    public List<CSVLine> getLinksData();

    public ZipService getZipService();

    public StateService getStateService();

    public List<CSVLine> getImageData();

    public Properties getLocalProperties();

    public String getPath(String property);
}
