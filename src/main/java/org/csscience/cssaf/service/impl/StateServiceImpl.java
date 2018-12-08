/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.util.List;
import org.csscience.cssaf.content.State;
import org.csscience.cssaf.dao.StateDao;
import org.csscience.cssaf.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author john
 */
@Service
@Transactional
public class StateServiceImpl implements StateService {

    @Autowired
    private StateDao stateDao;
    
    @Override
    public List<State> findAll() {
        return stateDao.findAll();
    }

    @Override
    public State findByLongName(String longName) {
        return stateDao.findByLongName(longName);
    }

    @Override
    public State findByShortName(String shortName) {
        return stateDao.findByShortName(shortName);
    }

    @Override
    public void addState(State state) {
        stateDao.addState(state);
    }
}
