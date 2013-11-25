/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.service;

import com.bwinparty.techsonar.handler.FileHandler;
import com.bwinparty.techsonar.util.AppConfig;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;

/**
 *
 * @author Lukas Prettenthaler
 */
public class VertX {

    private static final Log LOG = LogFactory.getLog(VertX.class);
    private static VertX instance;
    private final HttpServer server;
    private final RouteMatcher routeMatcher;
    private final Vertx vertx;
    private final SockJSServer sockJsServer;

    static {
        instance = new VertX();
    }

    private VertX() {
        final AppConfig config = AppConfig.getInstance();
        vertx = VertxFactory.newVertx();
        server = vertx.createHttpServer();
        routeMatcher = new RouteMatcher();
        server.requestHandler(routeMatcher);
        sockJsServer = vertx.createSockJSServer(server);
        registerNomatchHandler();
        server.listen(config.getInteger("webservice.webport", 8080));
        LOG.debug("VertX initialized");
    }

    public void registerGetHandler(final String path, final Handler<HttpServerRequest> handler) {
        routeMatcher.get(path, handler);
    }

    public void registerPostHandler(final String path, final Handler<HttpServerRequest> handler) {
        routeMatcher.post(path, handler);
    }

    public void registerPutHandler(final String path, final Handler<HttpServerRequest> handler) {
        routeMatcher.put(path, handler);
    }

    public void registerDeleteHandler(final String path, final Handler<HttpServerRequest> handler) {
        routeMatcher.delete(path, handler);
    }

    public void registerSockJsHandler(final String path, final Handler<SockJSSocket> handler) {
        sockJsServer.installApp(new JsonObject().putString("prefix", path), handler);
    }

    public long setTimer(final long timeout, final Handler<Long> handler) {
        return vertx.setTimer(timeout, handler);
    }
    
    public long setPeriodic(final long timeout, final Handler<Long> handler) {
        return vertx.setPeriodic(timeout, handler);
    }
    
    public void cancelTimer(final long timerid) {
        vertx.cancelTimer(timerid);
    }

    public EventBus getEventBus() {
        return vertx.eventBus();
    }
    
    public void shutdown() {
        server.close();
    }

    private void registerNomatchHandler() {
        try {
            routeMatcher.noMatch(new FileHandler());
        } catch (IOException ex) {
            LOG.error(ex);
        }
    }

    public static VertX getInstance() {
        return instance;
    }
}
