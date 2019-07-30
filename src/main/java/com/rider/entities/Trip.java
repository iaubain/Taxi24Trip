/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.rider.entities;

import biz.galaxy.commons.config.CommonErrorCodeConfig;
import biz.galaxy.commons.models.ErrorModel;
import biz.galaxy.commons.models.ErrorsListModel;
import biz.galaxy.commons.utilities.ErrorGeneralException;
import biz.galaxy.commons.utilities.IdGen;
import biz.galaxy.commons.utilities.UtilModel;
import com.rider.config.StatusConfig;
import com.rider.config.SystemConfig;
import com.rider.facades.BaseEntity;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Aubain
 */
@Entity
@Table(name = "Trip",
        indexes = {@Index(name = "idx_1", columnList = "status"),
            @Index(name = "idx_2", columnList = "creationTime"),
            @Index(name = "idx_3", columnList = "driverId"),
            @Index(name = "idx_4", columnList = "riderId"),
            @Index(name = "idx_5", columnList = "tripStatus")})
public class Trip extends BaseEntity implements Serializable, UtilModel{
    private static final long serialVersionUID = 1L;
    public static enum TripStatus{ONGOING, COMPLETED};
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "driverId", length = 255)
    private String driverId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "riderId", length = 255)
    private String riderId;
    @Column(name = "tripStatus")
    @Enumerated(EnumType.STRING)
    private TripStatus tripStatus;
    
    public Trip() {
        super();
    }
    
    @Override
    public void validateOb() throws ErrorGeneralException {
        ErrorsListModel errors = new ErrorsListModel();
        if(this == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Null object"));
        }
        
        if(riderId == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Field: riderId shouldn't be null"));
        }
        if(driverId == null){
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Field: driverId shouldn't be null"));
        }
        try {
            TripStatus ts = tripStatus;
        } catch (Exception e) {
            errors.addError(new ErrorModel(SystemConfig.SYSTEM_ID[0]+CommonErrorCodeConfig.VALIDATION_ERROR[0], CommonErrorCodeConfig.VALIDATION_ERROR[1], "Unsupported trip status. Supported values: "+TripStatus.values().toString()));
        }
        if(!errors.getErrors().isEmpty()){
            throw new ErrorGeneralException(errors);
        }
    }
    
    @Override
    public void prepare() throws Exception {
        if(super.getId() == null)
            super.setId(IdGen.SIMPLE());
        if(tripStatus == null)
            tripStatus = TripStatus.ONGOING;
        super.setCreationTime(new Date());
        super.setStatus(StatusConfig.ACTIVE);
    }
    
    public String getDriverId() {
        return driverId;
    }
    
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    
    public String getRiderId() {
        return riderId;
    }
    
    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }
    
    public TripStatus getTripStatus() {
        return tripStatus;
    }
    
    public void setTripStatus(TripStatus tripStatus) {
        this.tripStatus = tripStatus;
    }
    
}
