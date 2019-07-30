/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.rider.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author aubain
 */
public class Log {
    public static final void e(Class className, Throwable e){
        if(className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.error("Error",e);
        }
        
    }
    public static final void e(Class className, String message){
        if(message != null && className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.error(message);
        }
    }
    public static final void t(Class className, String trace){
        if(className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.trace(trace);
        }
        
    }
    public static final void t(Class className, Throwable e){
        if(className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.trace(e);
        }
    }
    public static final void f(Class className, String fatal){
        if(className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.fatal(fatal);
        }
        
    }
    public static final void f(Class className, Throwable e){
        if(className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.fatal(e);
        }
    }
    public static final void d(Class className, String message){
        if(message != null && className != null){
            Logger logger = LogManager.getLogger(className.getCanonicalName());
            logger.info(message);
        }
    }  
}
