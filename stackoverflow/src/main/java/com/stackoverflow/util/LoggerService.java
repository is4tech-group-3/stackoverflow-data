package com.stackoverflow.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

    public static void loggerInfo(HttpServletRequest request, HttpStatus status, String message){
        String addressIp = request.getRemoteAddr();
        logger.info("AddressIp: {}, Status: {}, Message: {}", addressIp, status.value(), message);
    }

    public static void loggerWarning(HttpServletRequest request, HttpStatus status, String message){
        String addressIp = request.getRemoteAddr();
        logger.warn("AddressIp: {}, Status: {}, Message: {}", addressIp, status.value(), message);
    }

    public static void loggerError(HttpServletRequest request, HttpStatus status, String message, Throwable throwable) {
        String addressIp = request.getRemoteAddr();
        logger.error("AddressIp: {}, Status: {}, Message: {}, Exception: {}", addressIp, status.value(), message, throwable.getMessage());
    }

    public static void loggerSevere(HttpServletRequest request, HttpStatus status, String message){
        String addressIp = request.getRemoteAddr();
        logger.error("AddressIp: {}, Status: {}, Message: {}", addressIp, status.value(), message);
    }

    public static void loggerDebug(String message){
        logger.info(message);
    }
}
