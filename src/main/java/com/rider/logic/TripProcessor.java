/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.rider.logic;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import biz.galaxy.commons.utilities.serializer.UtilSerializer;
import com.rider.config.StatusConfig;
import com.rider.config.SystemConfig;
import com.rider.entities.Trip;
import com.rider.facades.CommonQueries;
import com.rider.facades.GenericDao;
import com.rider.models.DriverModel;
import com.rider.models.RiderModel;
import com.rider.remote.ExternalService;
import com.rider.utilities.CacheManager;
import com.rider.utilities.Log;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class TripProcessor {
    @Autowired
            GenericDao dao;
    @Autowired
            CommonQueries cQuery;
    
    public List<Trip> createTrips(String body)throws ErrorGeneralException, Exception{
        List<Trip> trips;
        List<Trip> myTrips = new ArrayList<>();
        //Serialize the body
        try {
            trips = new UtilSerializer(Trip.class).serializeList(body);
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        //prepare the body payload
        try {
            if(trips == null || trips.isEmpty()){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Serialization produced null results."))));
            }
            for(Trip trip : trips){
                trip.prepare();
                trip.validateOb();
                
                if(cQuery.isTripCreated(trip.getDriverId()))
                    throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Trip already exist. "+trip.toString()))));
                
                //filter rider
                RiderModel rider = (RiderModel) CacheManager.GET(trip.getRiderId());
                if(rider == null){
                    //If nothing is found in the cache we query the component
                    RiderModel filter = new RiderModel();
                    filter.setId(trip.getRiderId());
                    List<RiderModel> mRds = ExternalService.FILTER_RIDER(filter);
                    if(mRds == null || mRds.isEmpty()){
                        throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.CONSTRAINT_ERROR[0], CommonErrorCodeConfig.CONSTRAINT_ERROR[1], "Rider was not found. "+trip.toString()))));
                    }
                    //If from URL they was a result we cache it, with a life time of 6 hours as cashe is configured to last its record 6 hours
                    rider = mRds.get(0);
                    CacheManager.ADD(rider.getId(), rider);
                }
                //filter driver
                DriverModel driver = (DriverModel) CacheManager.GET(trip.getDriverId());
                if(driver == null){
                    //If nothing is found in the cache we query the component
                    DriverModel filter = new DriverModel();
                    filter.setId(trip.getDriverId());
                    List<DriverModel> mRds = ExternalService.FILTER_DRIVER(filter);
                    if(mRds == null || mRds.isEmpty()){
                        throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.CONSTRAINT_ERROR[0], CommonErrorCodeConfig.CONSTRAINT_ERROR[1], "Driver was not found. "+trip.toString()))));
                    }
                    //If from URL they was a result we cache it, with a life time of 6 hours as cashe is configured to last its record 6 hours
                    driver = mRds.get(0);
                    CacheManager.ADD(driver.getId(), driver);
                }
                driver.setStatus("ONTRIP");
                List<DriverModel> editedDriver = ExternalService.EDIT_DRIVER(driver);
                myTrips.add(trip);
            }
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        try {
            return dao.save(myTrips);
        }  catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
    
    public List<Trip> completeTrip(String body)throws ErrorGeneralException, Exception{
        List<Trip> trips;
        List<Trip> myTrips = new ArrayList<>();
        //Serialize the body
        try {
            trips = new UtilSerializer(Trip.class).serializeList(body);
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        //prepare the body payload
        try {
            if(trips == null || trips.isEmpty()){
                throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Serialization produced null results."))));
            }
            for(Trip trip : trips){
                trip.validateOb();
                Trip checkTrip = dao.find(Trip.class, trip.getId());
                if(checkTrip == null)
                    throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Rider not found. "+trip.toString()))));
                checkTrip.setTripStatus(Trip.TripStatus.COMPLETED);
                myTrips.add(checkTrip);
                
                try {
                    //filter driver
                    DriverModel driver = (DriverModel) CacheManager.GET(trip.getDriverId());
                    if(driver == null){
                        //If nothing is found in the cache we query the component
                        DriverModel filter = new DriverModel();
                        filter.setId(trip.getDriverId());
                        List<DriverModel> mRds = ExternalService.FILTER_DRIVER(filter);
                        if(mRds != null && !mRds.isEmpty()){
                            //If from URL they was a result we cache it, with a life time of 6 hours as cashe is configured to last its record 6 hours
                            driver = mRds.get(0);
                            CacheManager.ADD(driver.getId(), driver);
                        }
                    }
                    if(driver != null){
                        driver.setStatus(StatusConfig.ACTIVE);
                        List<DriverModel> editedDriver = ExternalService.EDIT_DRIVER(driver);
                    }
                } catch (Exception e) {
                    Log.e(getClass(), e);
                }
            }
        } catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            e.printStackTrace(out);
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
        try {
            return dao.update(myTrips);
        }  catch (ErrorGeneralException e) {
            throw e;
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            throw new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "There was an internal issue while proccessing your request."))));
        }
    }
    
}
