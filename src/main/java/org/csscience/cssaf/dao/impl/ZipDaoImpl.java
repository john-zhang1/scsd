/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.dao.impl;

import java.util.List;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.dao.AbstractDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.csscience.cssaf.dao.ZipDao;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author john
 */
@Repository
public class ZipDaoImpl extends AbstractDao implements ZipDao{

    @SuppressWarnings("unchecked")
    @Override
    public List<Zip> findAll() {
        Criteria criteria = getSession().createCriteria(Zip.class);
        return (List<Zip>)criteria.list();
    }

    @Override
    public Zip findByZip(String zip) {
        Criteria criteria = getSession().createCriteria(Zip.class);
        // In case zip has pattern 05445
        int num = Integer.parseInt(zip);
        String numString = Integer.toString(num);
        criteria.add(Restrictions.eq("zip", numString));
        return (Zip)criteria.uniqueResult();
    }

    @Override
    public void saveZip(Zip zip) {
        persist(zip);
    }

    @Override
    public void update(Zip zip) {
        getSession().update(zip);
    }

    @Override
    public void deleteZipByZipcode(String zip) {
        Query query = getSession().createSQLQuery("delete from zipcode where zip = :zip");
        query.setString("zip", zip);
        query.executeUpdate();
    }

}
