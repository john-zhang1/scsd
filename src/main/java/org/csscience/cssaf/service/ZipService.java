/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.util.List;
import org.csscience.cssaf.content.Zip;

/**
 *
 * @author john
 */
public interface ZipService {

    public List<Zip> findAll();
    
    public Zip findByZip(String zipcode);
    
    public void saveZip(Zip zip);
    
    public void updateZip(Zip zip);
    
    public String getLatLon(String zipcode);

    public void deleteZipByZipcode(String shortName);

    public boolean isZipcodeUnique(String zipcode);

}
