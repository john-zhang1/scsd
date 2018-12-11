/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.saf;

/**
 *
 * @author john
 */
public class SAFExporter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SAF saf = new SAF();

        saf.createExitingDataSAF();
        saf.createNewDataSAF();
   }    
}
