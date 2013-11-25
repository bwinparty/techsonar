/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar;

import com.bwinparty.techsonar.model.Coordinates;
import com.bwinparty.techsonar.model.Item;
import com.bwinparty.techsonar.model.Item.ItemState;
import com.bwinparty.techsonar.model.Sector;
import com.bwinparty.techsonar.model.Track;
import com.bwinparty.techsonar.model.Version;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Lukas Prettenthaler
 */
public class DataFactory {
    public static Item createItem(String id){
        Item item = new Item();
        item.setShortcut("testshort");
        item.setId("test"+id);
        item.setCreated(new Date());
        item.setDescription("test1");
        item.setLicense("blah");
        item.setModified(new Date());
        item.setName("test2");
        item.setState(ItemState.USED);
        item.setSector_id("tasedtadg");
        item.setTrack_id("tesdgsfh");
        item.setUrl("http://test.com");
        item.setVendor("test3");
        Coordinates coordinates = new Coordinates();
        coordinates.setAngular(0.5);
        coordinates.setRadial(0.1);
        item.setCoordinates(coordinates);
        List<Version> versions = new ArrayList<>();
        Version version = new Version();
        version.setId("test4");
        version.setDetails("test5");
        versions.add(version);
        item.setVersions(versions);
        return item;
    }
    
    public static Track createTrack(String id){
        Track track = new Track();
        track.setId(id);
        track.setName("name"+id);
        track.setWeight(1);
        return track;
    }

    public static Sector createSector(String id){
        Sector sector = new Sector();
        sector.setId(id);
        sector.setName("name"+id);
        sector.setWeight(1);
        return sector;
    }
}
