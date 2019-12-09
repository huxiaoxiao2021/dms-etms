package com.jd.bluedragon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebHelper {
    
    private static Logger log = LoggerFactory.getLogger(Logger.class);
    
    public static String encodeURI(String uri) {
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void processAjax(final HttpServletResponse response, final AjaxAction action) {
        processAjax(response, action, AjaxAction.OPERATE_FAIL);
    }
    
    public static void processAjax(final HttpServletResponse response, final AjaxAction action,
            final String exceptionResult) {
        try {
            sendData(response, action.process());
        } catch (final Exception e) {
            log.error("Error occurred.", e);
            sendData(response, exceptionResult);
        }
    }
    
    public static void sendData(final HttpServletResponse response, final String data) {
        try {
            response.setContentType("text/html; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(data);
        } catch (Exception e) {
            log.error("Error occurred.", e);
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
            	log.error("Error occurred.", e);
            }
        }
    }
}
