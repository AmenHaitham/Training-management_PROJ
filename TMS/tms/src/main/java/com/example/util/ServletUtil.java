package com.example.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for common servlet operations
 */
public class ServletUtil {
    
    /**
     * Checks if the request is an AJAX request by checking the X-Requested-With header
     * 
     * @param request the HTTP request
     * @return true if it's an AJAX request, false otherwise
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
    
    /**
     * Gets the base URL of the application
     * 
     * @param request the HTTP request
     * @return the base URL of the application
     */
    public static String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
        
        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        
        url.append(contextPath);
        
        return url.toString();
    }
    
    /**
     * Gets a parameter value from the request, returning a default value if the parameter is null or empty
     * 
     * @param request the HTTP request
     * @param paramName the parameter name
     * @param defaultValue the default value to return if the parameter is null or empty
     * @return the parameter value or the default value
     */
    public static String getParameter(HttpServletRequest request, String paramName, String defaultValue) {
        String value = request.getParameter(paramName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Gets a parameter as Long from the request, returning a default value if the parameter is invalid
     * 
     * @param request the HTTP request
     * @param paramName the parameter name
     * @param defaultValue the default value to return if the parameter is invalid
     * @return the parameter value as Long or the default value
     */
    public static Long getLongParameter(HttpServletRequest request, String paramName, Long defaultValue) {
        String value = request.getParameter(paramName);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a parameter as Integer from the request, returning a default value if the parameter is invalid
     * 
     * @param request the HTTP request
     * @param paramName the parameter name
     * @param defaultValue the default value to return if the parameter is invalid
     * @return the parameter value as Integer or the default value
     */
    public static Integer getIntParameter(HttpServletRequest request, String paramName, Integer defaultValue) {
        String value = request.getParameter(paramName);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a parameter as Boolean from the request, returning a default value if the parameter is invalid
     * 
     * @param request the HTTP request
     * @param paramName the parameter name
     * @param defaultValue the default value to return if the parameter is invalid
     * @return the parameter value as Boolean or the default value
     */
    public static Boolean getBooleanParameter(HttpServletRequest request, String paramName, Boolean defaultValue) {
        String value = request.getParameter(paramName);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        
        return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value);
    }
} 