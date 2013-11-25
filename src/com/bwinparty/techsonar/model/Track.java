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

/**
 *
 * @author Lukas Prettenthaler
 */
@JsonIgnoreProperties({"_id"})
public class Track implements Updateable<Track> {
    private String _id;
    private String _name;
    private String _description;
    private String[] _tags;
    private Integer _weight;

    @Override
    public void update(final Track updates) {
        if(updates.getDescription() != null)
            this.setDescription(updates.getDescription());
        if(updates.getName() != null)
            this.setName(updates.getName());
        if(updates.getTags() != null)
            this.setTags(updates.getTags());
        if(updates.getWeight() != null)
            this.setWeight(updates.getWeight());
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
     * @return the _weight
     */
    public Integer getWeight() {
        return _weight;
    }

    /**
     * @param weight the _weight to set
     */
    public void setWeight(Integer weight) {
        this._weight = weight;
    }

    /**
     * @return the _tags
     */
    public String[] getTags() {
        return _tags;
    }

    /**
     * @param tags the _tags to set
     */
    public void setTags(String[] tags) {
        this._tags = tags;
    }
}
