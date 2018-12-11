/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.content.factory;

import org.csscience.cssaf.service.StateService;
import org.csscience.cssaf.service.ZipService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author john
 */
public class ContentServiceFactoryImpl extends ContentServiceFactory {

    @Autowired(required = true)
    private StateService stateService;
    @Autowired(required = true)
    private ZipService zipService;
    
    @Override
    public StateService getStateService() {
        return stateService;
    }

    @Override
    public ZipService getZipService() {
        return zipService;
    }

}
