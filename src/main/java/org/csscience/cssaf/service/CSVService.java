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

    public List<CSVLine> getCollectionData();

    /** Get a csv contains exiting samples with metadata updated */
    public List<CSVLine> getExitingDataCSV();

    /** Get a csv contains new samples */
    public List<CSVLine> getNewDataCSV();

    /** Adjust new data form removing unnecessary columns and complete state and spatial data
     * @return newCSV */
    public List<CSVLine> adjustedNewDataCSV();

    /** update existing sample data
     * @param existingSample
     * @param newSample
     * @return existingCSV */
    public CSVLine adjustedExistingDataCSV(CSVLine existingSample, CSVLine newSample);

    public List<CSVLine> getCollectionTaxonomyData();

    public List<CSVLine> getLinksData();

}
