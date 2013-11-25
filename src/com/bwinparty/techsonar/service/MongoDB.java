/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.service;

import com.bwinparty.techsonar.model.Item;
import com.bwinparty.techsonar.model.Sector;
import com.bwinparty.techsonar.model.Track;
import com.bwinparty.techsonar.util.AppConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.JacksonDBCollection;

/**
 *
 * @author Lukas Prettenthaler
 */
public final class MongoDB {

    private static final Log LOG = LogFactory.getLog(MongoDB.class);
    private static MongoDB instance;
    private DB database;
    private JacksonDBCollection<Item, String> coll_items;
    private JacksonDBCollection<Sector, String> coll_sectors;
    private JacksonDBCollection<Track, String> coll_tracks;

    static {
        instance = new MongoDB();
    }

    private MongoDB() {
        final AppConfig config = AppConfig.getInstance();
        final String uri = config.getString("mongodb.uri", "mongodb://127.0.0.1:27017/techradar");

        try {
            final MongoClientOptions.Builder options = new MongoClientOptions.Builder();
            options.autoConnectRetry(true);
            options.maxAutoConnectRetryTime(15); //s
            options.connectTimeout(10000); //ms
            options.socketTimeout(30000); //ms
            options.socketKeepAlive(false);
            options.connectionsPerHost(10);
            options.threadsAllowedToBlockForConnectionMultiplier(5);
            options.maxWaitTime(120000); //ms
            final MongoClientURI mongouri = new MongoClientURI(uri, options);
            final MongoClient mongo = new MongoClient(mongouri);
            database = mongo.getDB(mongouri.getDatabase());

            if ((mongouri.getUsername() != null) && (!mongouri.getUsername().trim().isEmpty()) && (!database.authenticate(mongouri.getUsername(), mongouri.getPassword()))) {
                throw new Exception("Unable to authenticate with MongoDB server.");
            }
            initCollections();
            ensureIndexes();
        } catch (Exception e) {
            database = null;
            LOG.error("Connection to MongoDB could not be established: " + e.getMessage(), e);
        }
        LOG.debug("MongoDB initialized");
    }

    private void initCollections() {
        coll_items = JacksonDBCollection.wrap(database.getCollection("items"), Item.class, String.class);
        coll_sectors = JacksonDBCollection.wrap(database.getCollection("sectors"), Sector.class, String.class);
        coll_tracks = JacksonDBCollection.wrap(database.getCollection("tracks"), Track.class, String.class);
    }

    private void dropCollections() {
        coll_items.dropIndexes();
        coll_items.drop();
        coll_sectors.dropIndexes();
        coll_sectors.drop();
        coll_tracks.dropIndexes();
        coll_tracks.drop();
    }

    private void ensureIndexes() {
        coll_items.ensureIndex(new BasicDBObject("id", 1), new BasicDBObject("unique", true));
        coll_items.ensureIndex(new BasicDBObject("shortcut", 1), new BasicDBObject("unique", true));
        coll_items.ensureIndex(new BasicDBObject("name", 1));
        coll_items.ensureIndex(new BasicDBObject("track_id", 1).append("sector_id", 1));
        
        coll_sectors.ensureIndex(new BasicDBObject("id", 1), new BasicDBObject("unique", true));
        coll_sectors.ensureIndex(new BasicDBObject("tags", 1));

        coll_tracks.ensureIndex(new BasicDBObject("id", 1), new BasicDBObject("unique", true));
        coll_tracks.ensureIndex(new BasicDBObject("tags", 1));
    }

    public DBCursor<Item> findItems() {
        return coll_items.find();
    }

    public DBCursor<Item> findItems(final String id) {
        return coll_items.find().is("id", id);
    }

    public DBCursor<Item> findItems(final String sectors, final String tracks) {
        final Object[] trackarray = tracks.split(":");
        final Object[] sectorarray = sectors.split(":");
        return coll_items.find().in("track_id", trackarray).and(DBQuery.in("sector_id", sectorarray));
    }

    public DBCursor<Track> findTracks() {
        return coll_tracks.find();
    }

    public DBCursor<Track> findTracks(final String tag) {
        return coll_tracks.find().in("tags", tag);
    }

    public DBCursor<Sector> findSectors() {
        return coll_sectors.find();
    }

    public DBCursor<Sector> findSectors(final String tag) {
        return coll_sectors.find().in("tags", tag);
    }

    public String createItem(final Item item) throws Exception {
        final String id = UUID.randomUUID().toString();
        item.setId(id);
        coll_items.insert(item);
        LOG.trace("inserted item to mongodb");
        return id;
    }

    public void updateItem(final Item item) throws Exception {
        final Item olditem = coll_items.findOne(DBQuery.is("id", item.getId()));
        if(olditem == null){
            return;
        }
        olditem.update(item);
        coll_items.update(DBQuery.is("id", olditem.getId()), olditem);
        LOG.trace("saved item to mongodb");
    }

    public void deleteItem(final Item item) {
        coll_items.remove(DBQuery.is("id", item.getId()));
        LOG.trace("deleted item from mongodb");
    }

    public String createTrack(final Track track) throws Exception {
        final String id = UUID.randomUUID().toString();
        track.setId(id);
        coll_tracks.insert(track);
        LOG.trace("inserted track to mongodb");
        return id;
    }

    public void updateTrack(final Track track) throws Exception {
        final Track oldtrack = coll_tracks.findOne(DBQuery.is("id", track.getId()));
        if(oldtrack == null){
            return;
        }
        oldtrack.update(track);
        coll_tracks.update(DBQuery.is("id", oldtrack.getId()), oldtrack);
        LOG.trace("saved track to mongodb");
    }

    public void deleteTrack(final Track track) {
        coll_tracks.remove(DBQuery.is("id", track.getId()));
        LOG.trace("deleted track from mongodb");
    }

    public String createSector(final Sector sector) throws Exception {
        final String id = UUID.randomUUID().toString();
        sector.setId(id);
        coll_sectors.insert(sector);
        LOG.trace("inserted sector to mongodb");
        return id;
    }

    public void updateSector(final Sector sector) throws Exception {
        final Sector oldsector = coll_sectors.findOne(DBQuery.is("id", sector.getId()));
        if(oldsector == null){
            return;
        }
        oldsector.update(sector);
        coll_sectors.update(DBQuery.is("id", oldsector.getId()), oldsector);
        LOG.trace("saved sector to mongodb");
    }

    public void deleteSector(final Sector sector) {
        coll_sectors.remove(DBQuery.is("id", sector.getId()));
        LOG.trace("deleted sector from mongodb");
    }

    public void resetCollections() {
        dropCollections();
        initCollections();
        ensureIndexes();
    }

    public static MongoDB getInstance() {
        return instance;
    }
}
