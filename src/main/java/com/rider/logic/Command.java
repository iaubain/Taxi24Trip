/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.rider.logic;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.DataFactory;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import biz.galaxy.commons.utilities.serializer.UtilSerializer;
import com.rider.config.CmdConfig;
import com.rider.config.HeaderConfig;
import com.rider.config.SystemConfig;
import com.rider.entities.Trip;
import com.rider.utilities.Log;
import com.rider.utilities.ReturnUtil;
import java.util.Arrays;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 *
 * @author aubain
 */
@Component
public class Command {
    @Autowired
            TripProcessor driverProcessor;
    @Autowired
            FilterProcessor filterProcessor;
    public ResponseEntity exec(Map<String,String> headers, String body, Map<String, String[]> params){
        try {
            CmdConfig command = CmdConfig.valueOf(headers.get(HeaderConfig.CMD).toUpperCase());
            switch(command){
                case CREATE_TRIP:
                    return ReturnUtil.isSuccess(new UtilSerializer(Trip.class).deSerialize(driverProcessor.createTrips(body)));
                case COMPLETE_TRIP:
                    return ReturnUtil.isSuccess(new UtilSerializer(Trip.class).deSerialize(driverProcessor.completeTrip(body)));
                case FILTER_TRIP:
                    return ReturnUtil.isSuccess(new UtilSerializer(Trip.class).deSerialize(filterProcessor.filterDriver(body)));
                default:
                    return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), DataFactory.errorObject(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[0], CommonErrorCodeConfig.GENERAL_PROCESSING_ERROR[1], "Invalid Command")))));                    
            }
        } catch(ErrorGeneralException e){
            return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }catch (Exception e) {
            Log.d(getClass(), e.getMessage());
            ErrorGeneralException error = new ErrorGeneralException(new ErrorsListModel(Arrays.asList(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "There is an issue with request. Contact your system administrator."))));
            return ReturnUtil.isFailed(HttpStatus.BAD_REQUEST.value(), error.getMessage());
        }
    }
}
