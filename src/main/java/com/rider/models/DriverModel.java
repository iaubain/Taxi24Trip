/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rider.models;

import com.rider.facades.BaseEntity;

/**
 *
 * @author Aubain
 */
public class DriverModel extends BaseEntity{
    private String driverNames;

    public DriverModel() {
        super();
    }

    public String getDriverNames() {
        return driverNames;
    }

    public void setDriverNames(String driverNames) {
        this.driverNames = driverNames;
    }
    
}
