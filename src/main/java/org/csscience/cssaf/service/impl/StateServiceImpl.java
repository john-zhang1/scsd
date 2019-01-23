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
import org.apache.log4j.Logger;

/**
 *
 * @author john
 */
@Service
@Transactional
public class StateServiceImpl implements StateService {

    /** log4j logger */
    private static final Logger log = Logger.getLogger(StateServiceImpl.class);

    @Autowired
    private StateDao stateDao;

    @Override
    public State findById(int id) {
        return stateDao.findById(id);
    }

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
    public void saveState(State state) {
        stateDao.saveState(state);
    }

    @Override
    public void updateState(State state) {
        if(state!=null){
            State st  = stateDao.findById(state.getStateId());
            st.setShortName(state.getShortName());
            st.setLongName(state.getLongName());
        }
    }

    @Override
    public void deleteStateByShortName(String shortName) {
        stateDao.deleteStateByShortName(shortName);
    }

    @Override
    public boolean isShortNameUnique(String shortName) {
        State state = findByShortName(shortName);
        return (state == null);
    }

    @Override
    public boolean isLongNameUnique(String longName) {
        State state = findByLongName(longName);
        return (state == null);
    }

    @Override
    public String combinedName(String shortName) {
        State state = findByShortName(shortName);
        String combinedState = state.getLongName() + " - " + state.getShortName();
        return combinedState;
    }
}
