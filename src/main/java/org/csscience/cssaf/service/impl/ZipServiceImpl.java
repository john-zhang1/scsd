/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.service.impl;

import java.util.List;
import org.apache.log4j.Logger;
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

    private static Logger log = Logger.getLogger(ZipServiceImpl.class);

    @Autowired
    private ZipDao zipDao;
    
    @Override
    public List<Zip> findAll() {
        return zipDao.findAll();
    }

    @Override
    public Zip findByZip(String zipcode) {
        return zipDao.findByZip(zipcode);
    }

    @Override
    public void addZip(Zip zip) {
        zipDao.addZip(zip);
    }

    @Override
    public void update(Zip zip) {
        zipDao.update(zip);
    }

    @Override
    public String getLatLon(String zipcode) {
        Zip zip = findByZip(zipcode);
        String latLon = zip.getLatitude() + "," + zip.getLongitude();
        return latLon;
    }
}