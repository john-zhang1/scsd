/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.dao;

import java.util.List;
import org.csscience.cssaf.content.State;

/**
 *
 * @author john
 */
public interface StateDao {

    public List<State> findAll();
    
    public State findByLongName(String longName);
    
    public State findByShortName(String shortName);
    
    public void addState(State state);
    
}
