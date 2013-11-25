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
public class Version {

    private String _id;
    private String _details;
    private VersionState _state;

    /**
     * @return the _state
     */
    public VersionState getState() {
        return _state;
    }

    /**
     * @param state the _state to set
     */
    public void setState(VersionState state) {
        this._state = state;
    }

    /**
     * @return the _id
     */
    public String getId() {
        return _id;
    }

    /**
     * @param id the _id to set
     */
    public void setId(String id) {
        this._id = id;
    }

    /**
     * @return the _details
     */
    public String getDetails() {
        return _details;
    }

    /**
     * @param details the _details to set
     */
    public void setDetails(String details) {
        this._details = details;
    }

    public enum VersionState {

        REJECTED,
        APPROVED,
        DEPRECATED
    }
}
