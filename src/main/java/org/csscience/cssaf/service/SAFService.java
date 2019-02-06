/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.util.List;
import org.csscience.cssaf.csv.CSVLine;


public interface SAFService {

    public void createExitingDataSAF();

    public void createNewDataSAF();

    public List<String> copyImages(CSVLine csv, String dest);

    public List<CSVLine> getImageData();

}
