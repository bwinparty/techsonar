/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.service;

import com.bwinparty.techsonar.DataFactory;
import com.bwinparty.techsonar.model.Item;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mongojack.DBCursor;

/**
 *
 * @author Lukas Prettenthaler
 */
public class MongoDBTest {
    private static MongoDB db;
    private static Item item;
    
    public MongoDBTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        item = DataFactory.createItem("0");
        db = MongoDB.getInstance();
        db.resetCollections();
        try {
            db.createItem(item);
        } catch (Exception ex) {
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of findItems method, of class MongoDB.
     */
    @Test
    public void testFindItems_0args() {
        System.out.println("findItems");
        DBCursor result = db.findItems();
        Item stored_item = (Item)result.next();
        assertEquals(1, result.count());
        //assertEquals(item, stored_item);
    }

    /**
     * Test of findItems method, of class MongoDB.
     */
    @Test
    public void testFindItems_String() {
        System.out.println("findItems");
        String shortcut = "";
        DBCursor expResult = null;
        DBCursor result = db.findItems(shortcut);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findItems method, of class MongoDB.
     */
    @Test
    public void testFindItems_String_String() {
        System.out.println("findItems");
        String track = "";
        String sector = "";
        DBCursor expResult = null;
        DBCursor result = db.findItems(track, sector);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findTracks method, of class MongoDB.
     */
    @Test
    public void testFindTracks() {
        System.out.println("findTracks");
        DBCursor expResult = null;
        DBCursor result = db.findTracks();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findSectors method, of class MongoDB.
     */
    @Test
    public void testFindSectors() {
        System.out.println("findSectors");
        DBCursor expResult = null;
        DBCursor result = db.findSectors();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of udpateItem method, of class MongoDB.
     */
    @Test
    public void testUpdateItem() {
        System.out.println("saveItem");
        Item item = null;
        try {
            db.updateItem(item);
        } catch (Exception ex) {
        }
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInstance method, of class MongoDB.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        MongoDB expResult = null;
        MongoDB result = MongoDB.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}