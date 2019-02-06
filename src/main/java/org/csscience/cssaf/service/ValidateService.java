/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public interface ValidateService {

    public Map<String, ArrayList> validatePhotoNameFormat(String sourceDir);

    public Map<String, ArrayList> validateCSV(File file);
    
    public Map<String, ArrayList> validateZipcode();
}
