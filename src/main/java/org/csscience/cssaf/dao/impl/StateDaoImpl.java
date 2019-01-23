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
import org.hibernate.Query;

/**
 *
 * @author john
 */
@Repository
public class StateDaoImpl extends AbstractDao implements StateDao{

    @Override
    public State findById(int id) {
        Criteria criteria = getSession().createCriteria(State.class);
        criteria.add(Restrictions.eq("stateId", id));
        return (State)criteria.uniqueResult();
    }
    
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
    public void saveState(State state) {
        persist(state);
    }

    @Override
    public void deleteStateByShortName(String shortName) {
        Query query = getSession().createSQLQuery("delete from states where shortName = :shortName");
        query.setString("shortName", shortName);
        query.executeUpdate();
    }
}
