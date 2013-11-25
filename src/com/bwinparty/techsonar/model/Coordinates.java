/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.model;

/**
 *
 * @author Lukas Prettenthaler
 */
public class Coordinates {
    private Double _radial;
    private Double _angular;

    /**
     * @return the _radial
     */
    public Double getRadial() {
        return _radial;
    }

    /**
     * @param radial the _radial to set
     */
    public void setRadial(Double radial) {
        this._radial = radial;
    }

    /**
     * @return the _angular
     */
    public Double getAngular() {
        return _angular;
    }

    /**
     * @param angular the _angular to set
     */
    public void setAngular(Double angular) {
        this._angular = angular;
    }
}
