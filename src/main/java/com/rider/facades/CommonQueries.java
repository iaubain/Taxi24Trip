/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.rider.facades;

import biz.galaxy.commons.utilities.ErrorGeneralException;
import com.rider.config.StatusConfig;
import com.rider.entities.Trip;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.rider.utilities.QueryWrapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class CommonQueries{
    @Autowired
            GenericDao genericDao;
    public boolean isTripCreated(String driverId)throws ErrorGeneralException, Exception{
        QueryWrapper queryWrapper = new QueryWrapper("select B from Trip B where B.driverId = :driverId and B.tripStatus = :tripStatus Order by B.creationTime DESC");
        queryWrapper.setParameter("driverId", driverId);
        queryWrapper.setParameter("tripStatus", Trip.TripStatus.ONGOING);
        queryWrapper.setPageSize(1);
        List<Trip> mDrvs = genericDao.findWithNamedQuery(queryWrapper);
        return !(mDrvs == null || mDrvs.isEmpty());
    }
    
    public List<Trip> availableDrivers()throws ErrorGeneralException, Exception{
        QueryWrapper queryWrapper = new QueryWrapper("select B from Driver B where B.status = :status Order by B.creationTime DESC");
        queryWrapper.setParameter("status", StatusConfig.ACTIVE);
        return genericDao.findWithNamedQuery(queryWrapper);
    }
    
    public <T> List<T> filter(QueryWrapper queryWrapper)throws ErrorGeneralException, Exception{
        return genericDao.findWithNamedQuery(queryWrapper);
    }
}
