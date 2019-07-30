/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.rider.logic;

import biz.galaxy.commons.utilities.serializer.UtilSerializer;
import com.rider.config.SystemConfig;
import com.rider.entities.Trip;
import com.rider.facades.CommonQueries;
import com.rider.facades.GenericDao;
import com.rider.utilities.Log;
import com.rider.utilities.UtilInitiator;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class ComponetInitiator {
    @Autowired
            GenericDao dao;
    @Autowired
            CommonQueries cQuery;
    @Autowired
            TripProcessor tripProcessor;
    @PostConstruct
    public void init() {
        //Initiate component
        try {
            Thread t = new Thread(new UtilInitiator());
            t.start();
        } catch (Exception e) {
            Log.e(getClass(), SystemConfig.SYSTEM_ID[1]+"_"+e.getMessage());
        }
        //create default values
        List<Trip> mTrips = new ArrayList<>();
        for(int i = 1; i <= 6; i++){
            try {
                if(cQuery.isTripCreated("12"+i))
                    continue;
                
                Trip trip = new Trip();
                trip.setDriverId("12"+i);
                trip.setRiderId("13"+i);
                trip.setId("tr"+i);
                mTrips.add(trip);
            } catch (Exception e) {
                Log.e(getClass(), SystemConfig.SYSTEM_ID[1]+"_"+e.getMessage());
            }
        }
        
        try {
            tripProcessor.createTrips(new UtilSerializer(Trip.class).deSerialize(mTrips));
        } catch (Exception e) {
            Log.e(getClass(), SystemConfig.SYSTEM_ID[1]+"_"+e.getMessage());
        }
    }
}