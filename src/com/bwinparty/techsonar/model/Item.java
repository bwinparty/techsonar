/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.model;

import com.bwinparty.techsonar.util.Updateable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Lukas Prettenthaler
 */
@JsonIgnoreProperties({"_id"})
public class Item implements Updateable<Item> {

    private String _id;
    private String _shortcut;
    private String _name;
    private String _description;
    private Coordinates _coordinates;
    private List<Version> _versions;
    private String _track_id;
    private String _sector_id;
    private String _vendor;
    private String _url;
    private String _license;
    private Date _created;
    private Date _modified;
    private ItemState _state;

    @Override
    public void update(final Item updates) {
        this.setModified(new Date());
        if (updates.getCoordinates() != null) {
            this.setCoordinates(updates.getCoordinates());
        }
        if (updates.getDescription() != null) {
            this.setDescription(updates.getDescription());
        }
        if (updates.getLicense() != null) {
            this.setLicense(updates.getLicense());
        }
        if (updates.getName() != null) {
            this.setName(updates.getName());
        }
        if (updates.getSector_id() != null) {
            this.setSector_id(updates.getSector_id());
        }
        if (updates.getShortcut() != null) {
            this.setShortcut(updates.getShortcut());
        }
        if (updates.getTrack_id() != null) {
            this.setTrack_id(updates.getTrack_id());
        }
        if (updates.getUrl() != null) {
            this.setUrl(updates.getUrl());
        }
        if (updates.getVendor() != null) {
            this.setVendor(updates.getVendor());
        }
        if (updates.getVersions() != null) {
            this.setVersions(updates.getVersions());
        }
        if (updates.getState() != null) {
            this.setState(updates.getState());
        }
    }

    /**
     * @return the _id
     */
    public String getId() {
        return _id;
    }

    /**
     * @param shortcut the _id to set
     */
    public void setId(String id) {
        this._id = id;
    }

    /**
     * @return the _versions
     */
    public List<Version> getVersions() {
        return _versions;
    }

    /**
     * @param shortcut the _versions to set
     */
    public void setVersions(List<Version> versions) {
        this._versions = versions;
    }

    /**
     * @return the _shortcut
     */
    public String getShortcut() {
        return _shortcut;
    }

    /**
     * @param shortcut the _shortcut to set
     */
    public void setShortcut(String shortcut) {
        this._shortcut = shortcut;
    }

    /**
     * @return the _name
     */
    public String getName() {
        return _name;
    }

    /**
     * @param name the _name to set
     */
    public void setName(String name) {
        this._name = name;
    }

    /**
     * @return the _description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @param description the _description to set
     */
    public void setDescription(String description) {
        this._description = description;
    }

    /**
     * @return the _coordinates
     */
    public Coordinates getCoordinates() {
        return _coordinates;
    }

    /**
     * @param coordinates the _coordinates to set
     */
    public void setCoordinates(Coordinates coordinates) {
        this._coordinates = coordinates;
    }

    /**
     * @return the _track_id
     */
    public String getTrack_id() {
        return _track_id;
    }

    /**
     * @param track_id the _track_id to set
     */
    public void setTrack_id(String track_id) {
        this._track_id = track_id;
    }

    /**
     * @return the _sector_id
     */
    public String getSector_id() {
        return _sector_id;
    }

    /**
     * @param sector_id the _sector_id to set
     */
    public void setSector_id(String sector_id) {
        this._sector_id = sector_id;
    }

    /**
     * @return the _vendor
     */
    public String getVendor() {
        return _vendor;
    }

    /**
     * @param vendor the _vendor to set
     */
    public void setVendor(String vendor) {
        this._vendor = vendor;
    }

    /**
     * @return the _url
     */
    public String getUrl() {
        return _url;
    }

    /**
     * @param url the _url to set
     */
    public void setUrl(String url) {
        this._url = url;
    }

    /**
     * @return the _license
     */
    public String getLicense() {
        return _license;
    }

    /**
     * @param license the _license to set
     */
    public void setLicense(String license) {
        this._license = license;
    }

    /**
     * @return the _created
     */
    public Date getCreated() {
        return _created;
    }

    /**
     * @param created the _created to set
     */
    public void setCreated(Date created) {
        this._created = created;
    }

    /**
     * @return the _modified
     */
    public Date getModified() {
        return _modified;
    }

    /**
     * @param modified the _modified to set
     */
    public void setModified(Date modified) {
        this._modified = modified;
    }

    /**
     * @return the _state
     */
    public ItemState getState() {
        return _state;
    }

    /**
     * @param state the _state to set
     */
    public void setState(ItemState state) {
        this._state = state;
    }

    public enum ItemState {

        NEW,
        TRASHED,
        USED
    }
}
