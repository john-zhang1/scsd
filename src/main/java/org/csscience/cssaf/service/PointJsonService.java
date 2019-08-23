/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import org.csscience.cssaf.csv.CSVLine;

public interface PointJsonService {

    boolean writeJsonFile();

    CSVLine getPoint(CSVLine line);

}
