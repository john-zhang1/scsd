package org.csscience.cssaf.content;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "states", uniqueConstraints = {
    @UniqueConstraint(columnNames = "ID"),
    @UniqueConstraint(columnNames = "shortname"),
    @UniqueConstraint(columnNames = "longname")})
public class State implements Serializable {

    private static final long serialVersionUID = 1L;

    public State() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer stateId;

    @NotNull
    @Size(min = 2, max = 2)
    @Column(name = "shortname", unique = true, nullable = false, length = 2)
    private String shortName;
    
    @NotNull
    @Size(min = 1)
    @Column(name = "longname", unique = true, nullable = false, length = 25)
    private String longName;

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }
}