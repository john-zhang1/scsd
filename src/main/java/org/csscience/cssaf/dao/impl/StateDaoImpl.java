/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.dao.impl;

import java.util.List;
import org.csscience.cssaf.content.State;
import org.csscience.cssaf.dao.AbstractDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.csscience.cssaf.dao.StateDao;

/**
 *
 * @author john
 */
@Repository
public class StateDaoImpl extends AbstractDao implements StateDao{

    @SuppressWarnings("unchecked")
    @Override
    public List<State> findAll() {
        Criteria criteria = getSession().createCriteria(State.class);
        return (List<State>)criteria.list();
    }

    @Override
    public State findByLongName(String longName) {
        Criteria criteria = getSession().createCriteria(State.class);
        criteria.add(Restrictions.eq("longName", longName));
        return (State)criteria.uniqueResult();
    }

    @Override
    public State findByShortName(String shortName) {
        Criteria criteria = getSession().createCriteria(State.class);
        criteria.add(Restrictions.eq("shortName", shortName));
        return (State)criteria.uniqueResult();
    }

    @Override
    public void addState(State state) {
        persist(state);
    }
   
}
