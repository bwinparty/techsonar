/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.handler;

import com.bwinparty.techsonar.service.MongoDB;
import com.bwinparty.techsonar.util.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

/**
 *
 * @author Lukas Prettenthaler
 */
public class RestHandler implements Handler<HttpServerRequest> {

    private static final Log LOG = LogFactory.getLog(RestHandler.class);
    private final ObjectMapper mapper;
    private final MongoDB db;
    private final String crossdomainpolicy;

    public RestHandler() {
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
        LOG.debug("got GET request path: " + req.path());
        final String endpoint = req.params().get("endpoint");
        String output;
        try {
            switch (endpoint) {
                case "servertime":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    output = mapper.writeValueAsString(new Date());
                    break;
                case "items":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("id")) {
                        output = mapper.writeValueAsString(db.findItems(req.params().get("id")).toArray());
                    } else if (req.params().contains("sectors") && req.params().contains("tracks")) {
                        output = mapper.writeValueAsString(db.findItems(req.params().get("sectors"), req.params().get("tracks")).toArray());
                    } else {
                        output = mapper.writeValueAsString(db.findItems().toArray());
                    }
                    break;
                case "tracks":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("tag")) {
                        output = mapper.writeValueAsString(db.findTracks(req.params().get("tag")).toArray());
                    } else {
                        output = mapper.writeValueAsString(db.findTracks().toArray());
                    }
                    break;
                case "sectors":
                    req.response().putHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    req.response().putHeader("Pragma", "no-cache");
                    if (req.params().contains("tag")) {
                        output = mapper.writeValueAsString(db.findSectors(req.params().get("tag")).toArray());
                    } else {
                        output = mapper.writeValueAsString(db.findSectors().toArray());
                    }
                    break;
                default:
                    output = "{\"error\":\"wrong endpoint\"}";
            }
        } catch (JsonProcessingException ex) {
            output = "{\"error\":\"server error\"}";
            LOG.warn("could not translate object to json " + ex.getMessage());
        }
        req.response().end(output);
    }
}
