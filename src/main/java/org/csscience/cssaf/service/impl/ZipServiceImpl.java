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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.csscience.cssaf.service.ZipcodeService;

/**
 *
 * @author john
 */
@Service
@Transactional
public class ZipServiceImpl implements ZipcodeService {

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
    public void saveZip(Zip zip) {
        zipDao.saveZip(zip);
    }

    @Override
    public void updateZip(Zip zip) {
        zipDao.update(zip);
    }

    @Override
    public void deleteZipByZipcode(String zipcode) {
        zipDao.deleteZipByZipcode(zipcode);
    }

    @Override
    public boolean isZipcodeUnique(String zipcode) {
        Zip zip = findByZip(zipcode);
        return (zip == null);
    }

    @Override
    public String getLatLon(String zipcode) {
        Zip zip = findByZip(zipcode);
        String latLon = null;
        try{
            latLon = zip.getLatitude() + "," + zip.getLongitude();
        }catch(Exception e){
            System.out.println(e);
        }
        
        return latLon;
    }
}
