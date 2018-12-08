/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.dao;

import java.math.BigDecimal;
import java.util.List;
import org.csscience.cssaf.content.Zip;

/**
 *
 * @author john
 */
public interface ZipDao {

    public List<Zip> findAll();
    
    public Zip findByZip(String zip);
    
    public void addZip(Zip zip);
    
    public void update(Zip zip);

}
