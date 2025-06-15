package com.alexlitovsky.bugs.sse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoggingFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);
	private static final String FILLER = "---";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
   	
    	String requestIP = null;
    	String method = null;
    	String path = null;
    	String params = null;
    	String sessionId = null;
    	int responseStatus;

		HttpServletRequest httpServletRequest = (HttpServletRequest)request;
    	HttpServletResponse httpServletResponse = (HttpServletResponse)response;

    	requestIP = httpServletRequest.getRemoteAddr();
    	method = httpServletRequest.getMethod();
        path = httpServletRequest.getRequestURI();
    	params = requestParamsToString(httpServletRequest);
    	sessionId = httpServletRequest.getSession(false) == null ? null : httpServletRequest.getSession().getId();
    	
    	LOGGER.info(tabDelimited("Request", FILLER, requestIP, method, path, params, sessionId));
    	
    	try {
    		chain.doFilter(request, response);
    	}
    	finally {
    		
        	responseStatus = httpServletResponse.getStatus();
        	sessionId = httpServletRequest.getSession(false) == null ? null : httpServletRequest.getSession().getId();

	    	LOGGER.info(tabDelimited("Response", responseStatus, requestIP, method, path, params, sessionId));
    	}
	}

	private String requestParamsToString(HttpServletRequest request) {

        StringBuilder sb = new StringBuilder();

        boolean firstParam = true;
        
        for (String name : request.getParameterMap().keySet()) {
        	for (String value : request.getParameterMap().get(name)) {
                try {
                	if (! firstParam) {
                		sb.append("&");
                	}
	                sb.append(URLEncoder.encode(name, "UTF-8"));
	                sb.append("=");
            		sb.append(URLEncoder.encode(value, "UTF-8"));
	                firstParam = false;
                } catch (UnsupportedEncodingException ex) {
                	// OK
                }
        	}
        }
        
        return sb.toString();
    }
	
	private static String tabDelimited(Object... valuesToLog) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < valuesToLog.length; i++) {
			if (i > 0) {
				sb.append("\t");
			}
			sb.append(valuesToLog[i]);
		}
		return sb.toString();
	}
}
