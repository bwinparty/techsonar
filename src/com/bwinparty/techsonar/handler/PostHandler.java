/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.handler;

import com.bwinparty.techsonar.model.Item;
import com.bwinparty.techsonar.model.Sector;
import com.bwinparty.techsonar.model.Track;
import com.bwinparty.techsonar.service.MongoDB;
import com.bwinparty.techsonar.util.AppConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

/**
 *
 * @author Lukas Prettenthaler
 */
public class PostHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(PostHandler.class);
    private final ObjectMapper mapper;
    private final String crossdomainpolicy;
    private final MongoDB db;

    public PostHandler() {
        mapper = new ObjectMapper();
        db = MongoDB.getInstance();
        final AppConfig config = AppConfig.getInstance();
        crossdomainpolicy = config.getString("webservice.cross-domain-policy", "*");
    }

    @Override
    public void handle(final HttpServerRequest req) {
        req.response().setStatusCode(200);
        req.response().putHeader("Content-Type", "application/json; charset=utf-8");
        req.response().putHeader("Access-Control-Allow-Origin", crossdomainpolicy);
        req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        req.response().putHeader("Pragma", "no-cache");
        LOG.debug("got POST request path: " + req.path());
        final String endpoint = req.params().get("endpoint");
        req.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(final Buffer buffer) {
                String output;
                String id;
                try {
                    switch (endpoint) {
                        case "items":
                            final Item item = mapper.readValue(buffer.getBytes(), Item.class);
                            item.setCreated(new Date());
                            id = db.createItem(item);
                            output = "{\"message\":\"success\", \"id\":\""+id+"\"}";
                            break;
                        case "tracks":
                            final Track track = mapper.readValue(buffer.getBytes(), Track.class);
                            id = db.createTrack(track);
                            output = "{\"message\":\"success\", \"id\":\""+id+"\"}";
                            break;
                        case "sectors":
                            final Sector sector = mapper.readValue(buffer.getBytes(), Sector.class);
                            id = db.createSector(sector);
                            output = "{\"message\":\"success\", \"id\":\""+id+"\"}";
                            break;
                        default:
                            output = "{\"error\":\"wrong endpoint\"}";
                    }
                } catch (Exception ex) {
                    output = "{\"error\":\"server error\"}";
                    LOG.warn("could not translate object to json " + ex.getMessage());
                }
                req.response().end(output);
            }
        });
    }
}
