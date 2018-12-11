/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.app.marker;

/**
 *
 * @author john
 */
public class PointsExporter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PointJson p = new PointJson();
        p.writeJsonFile();
    }
    
}
