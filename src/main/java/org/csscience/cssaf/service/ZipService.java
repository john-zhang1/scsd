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
    
    public void addZip(Zip zip);
    
    public void update(Zip zip);
    
    public String getLatLon(String zipcode);

}