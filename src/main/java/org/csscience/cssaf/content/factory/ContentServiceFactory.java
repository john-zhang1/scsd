/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.content.factory;

import org.csscience.cssaf.service.StateService;
import org.csscience.cssaf.service.ZipcodeService;

/**
 *
 * @author john
 */
public abstract class ContentServiceFactory {
    
    public abstract StateService getStateService();
    
    public abstract ZipcodeService getZipService();

}
