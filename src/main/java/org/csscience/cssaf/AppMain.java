/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf;

import java.util.List;
import org.csscience.cssaf.configuration.AppConfig;
import org.csscience.cssaf.content.State;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.service.StateService;
import org.csscience.cssaf.service.ZipService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 *
 * @author john
 */
public class AppMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        StateService stateService = (StateService) context.getBean(StateService.class);
        ZipService zipService = (ZipService) context.getBean(ZipService.class);
        
        List<State> sl = stateService.findAll();
        State s = stateService.findByShortName("MD");
        String cb = s.combinedName();
        List<Zip> z = zipService.findAll();
        Zip zip = zipService.findByZip("73072");
        
        String w = zip.getCity();
        
        System.out.print(sl);
        
        context.close();
    }
    
}
