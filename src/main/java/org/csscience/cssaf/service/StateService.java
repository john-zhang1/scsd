/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service;

import java.util.List;
import org.csscience.cssaf.content.State;

/**
 *
 * @author john
 */
public interface StateService {

    public State findById(int id);

    public List<State> findAll();
    
    public State findByLongName(String longName);
    
    public State findByShortName(String shortName);
    
    public void saveState(State state);

    public void updateState(State state);

    public void deleteStateByShortName(String shortName);
    
    public String combinedName(String shortName);
    
    public boolean isShortNameUnique(String shortName);

    public boolean isLongNameUnique(String longName);

}
