/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.util.List;
import org.csscience.cssaf.csv.CSVLine;

public interface CSVService {

    public List<CSVLine> getCSDData();

    public List<CSVLine> getCSDData(String sessionID);

    public List<CSVLine> getCollectionData();

    public List<CSVLine> getCollectionData(String sessionID);

    /** Get a csv contains exiting samples with metadata updated */
    public List<CSVLine> getExitingDataCSV();

    public List<CSVLine> getExitingDataCSV(String sessionID);

    /** Get a csv contains new samples */
    public List<CSVLine> getNewDataCSV();

    public List<CSVLine> getNewDataCSV(String sessionID);

    /** Adjust new data form removing unnecessary columns and complete state and spatial data
     * @return newCSV */
    public List<CSVLine> adjustedNewDataCSV();

    public List<CSVLine> adjustedNewDataCSV(String sessionID);

    /** update existing sample data
     * @param existingSample
     * @param newSample
     * @return existingCSV */
    public CSVLine adjustedExistingDataCSV(CSVLine existingSample, CSVLine newSample);

    public List<CSVLine> getCollectionTaxonomyData();

    public List<CSVLine> getCollectionTaxonomyData(String sessionID);

    public List<CSVLine> getLinksData();

    public List<CSVLine> getLinksData(String sessionID);

}
