/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.util.List;
import java.util.Properties;
import org.csscience.cssaf.csv.CSVLine;

public interface CSBatchService {

    public List<CSVLine> getCollectionData();

    public List<CSVLine> getCollectionTaxonomyData();

    public List<CSVLine> getCSDData(String path);

    public List<CSVLine> getLinksData();

    public List<CSVLine> getImageData();

    public Properties getLocalProperties();

    public String getPath(String property);    

    public List<CSVLine> getNewDataCSV();

    public List<CSVLine> getExitingDataCSV();
}
