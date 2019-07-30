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
public class RiderModel extends BaseEntity{
    private String riderNames;

    public RiderModel() {
        super();
    }

    public String getRiderNames() {
        return riderNames;
    }

    public void setRiderNames(String riderNames) {
        this.riderNames = riderNames;
    }
    
}
