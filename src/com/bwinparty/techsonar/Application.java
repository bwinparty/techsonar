/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar;

import com.bwinparty.techsonar.handler.DeleteHandler;
import com.bwinparty.techsonar.handler.PostHandler;
import com.bwinparty.techsonar.handler.PutHandler;
import com.bwinparty.techsonar.handler.RestHandler;
import com.bwinparty.techsonar.service.VertX;
import java.util.concurrent.CountDownLatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.logging.julbridge.JULLog4jBridge;

/**
 *
 * @author Lukas Prettenthaler
 */
public class Application {

    private static final Log LOG = LogFactory.getLog(Application.class);
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private final VertX vertx;
    private final Object sync = new Object();

    public Application() {
        vertx = VertX.getInstance();
        initHandlers();
    }

    private void initHandlers() {
        vertx.registerDeleteHandler("/rest/:endpoint", new DeleteHandler());//delete
        vertx.registerPutHandler("/rest/:endpoint", new PutHandler());//update
        vertx.registerPostHandler("/rest/:endpoint", new PostHandler());//create
        vertx.registerGetHandler("/rest/:endpoint", new RestHandler());//read
        vertx.registerGetHandler("/rest/:endpoint/id/:id", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/tag/:tag", new RestHandler());
        vertx.registerGetHandler("/rest/:endpoint/sectors/:sectors/tracks/:tracks", new RestHandler());
        //vertx.registerSockJsHandler("/stream", new SockJsHandler());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JULLog4jBridge.assimilate();
        DOMConfigurator.configureAndWatch(System.getProperty("user.dir") + "/log4j.xml", 60000);
        final Application main = new Application();
        LOG.info("Application started");
        main.addShutdownHook();
        main.block();
    }

    private void block() {
        while (true) {
            try {
                stopLatch.await();
                break;
            } catch (InterruptedException e) {
                //Ignore
            }
        }
    }

    private void unblock() {
        stopLatch.countDown();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Application shutting down...");
                synchronized (sync) {
                    //shutdown
                }
                vertx.shutdown();
            }
        });
    }
}
