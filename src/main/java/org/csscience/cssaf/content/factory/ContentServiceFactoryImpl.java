/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.content.factory;

import org.csscience.cssaf.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.csscience.cssaf.service.ZipcodeService;

/**
 *
 * @author john
 */
public class ContentServiceFactoryImpl extends ContentServiceFactory {

    @Autowired(required = true)
    private StateService stateService;
    @Autowired(required = true)
    private ZipcodeService zipService;
    
    @Override
    public StateService getStateService() {
        return stateService;
    }

    @Override
    public ZipcodeService getZipService() {
        return zipService;
    }

}
