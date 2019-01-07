/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.validation;

import java.math.BigDecimal;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.service.CSService;
import org.csscience.cssaf.service.ZipService;
import org.csscience.cssaf.service.impl.CSServiceImpl;

/**
 *
 * @author john
 */
public class Validator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Validation v = new Validation();
        v.validatePhotos();
        v.validateCSV();
        v.validateZipcode();

//        CSService servicesFactory = CSServiceImpl.getInstance();
//        ZipService zipService = servicesFactory.getZipService();
//        
//        Zip zip = new Zip();
//        zip.setCity("Homer Glen");
//        zip.setLatitude(BigDecimal.valueOf(41.600033));
//        zip.setLongitude(BigDecimal.valueOf(-87.938110));
//        zip.setZip("60491");
//        zip.setShortState("IL");
//        zipService.addZip(zip);
    }
    
}
