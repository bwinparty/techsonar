/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Lukas Prettenthaler
 */
package com.bwinparty.techsonar.util;

/**
 *
 * @author Lukas Prettenthaler
 */
public interface Updateable<T> {
    void update(T updates);
}
