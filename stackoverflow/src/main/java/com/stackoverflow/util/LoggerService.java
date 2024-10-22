package com.stackoverflow.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

    private static final String LOG_FORMAT = "AddressIp: {}, Status: {}, Message: {}";
    private static final String LOG_FORMAT_WITH_EXCEPTION = "AddressIp: {}, Status: {}, Message: {}, Exception: {}";

    private LoggerService() {
    }

    public static void loggerInfo(HttpServletRequest request, HttpStatus status, String message) {
        String addressIp = request.getRemoteAddr();
        logger.info(LOG_FORMAT, addressIp, status.value(), message);
    }

    public static void loggerWarning(HttpServletRequest request, HttpStatus status, String message) {
        String addressIp = request.getRemoteAddr();
        logger.warn(LOG_FORMAT, addressIp, status.value(), message);
    }

    public static void loggerError(HttpServletRequest request, HttpStatus status, String message, Throwable throwable) {
        String addressIp = request.getRemoteAddr();
        logger.error(LOG_FORMAT_WITH_EXCEPTION, addressIp, status.value(), message, throwable.getMessage());
    }

    public static void loggerSevere(HttpServletRequest request, HttpStatus status, String message) {
        String addressIp = request.getRemoteAddr();
        logger.error(LOG_FORMAT, addressIp, status.value(), message);
    }

    public static void loggerDebug(String message) {
        logger.info(message);
    }

}
