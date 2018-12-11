/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.validation;

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
    }
    
}
