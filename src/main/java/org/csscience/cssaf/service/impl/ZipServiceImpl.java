/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.util.List;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.dao.ZipDao;
import org.csscience.cssaf.service.ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author john
 */
@Service
@Transactional
public class ZipServiceImpl implements ZipService {

    @Autowired
    private ZipDao zipDao;
    
    @Override
    public List<Zip> findAll() {
        return zipDao.findAll();
    }

    @Override
    public Zip findByZip(String zip) {
        return zipDao.findByZip(zip);
    }

    @Override
    public void addZip(Zip zip) {
        zipDao.addZip(zip);
    }

    @Override
    public void update(Zip zip) {
        zipDao.update(zip);
    }
}
