/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.content;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author john
 */
@Entity
@Table(name = "zipcode", uniqueConstraints = {
    @UniqueConstraint(columnNames = "ID"),
    @UniqueConstraint(columnNames = "ZIP")})
public class Zip implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public Zip() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer id;
    
    @NotNull
    @Column(name = "ZIP", unique = true, nullable = false)
    private String zip;
    
    @NotNull
    @Column(name = "CITY", nullable = false)
    private String city;
    
    @NotNull
    @Column(name = "SHORTSTATE", nullable = false)
    private String shortState;
    
    @NotNull
    @Column(name = "LATITUDE")
    private BigDecimal latitude;
    
    @NotNull
    @Column(name = "LONGITUDE")
    private BigDecimal longitude;

    public Integer getId() {
        return id;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getShortState() {
        return shortState;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setShortState(String shortState) {
        this.shortState = shortState;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
