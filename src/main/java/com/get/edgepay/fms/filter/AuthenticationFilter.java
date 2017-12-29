package com.get.edgepay.fms.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.get.edgepay.fms.constant.FmsCacheConstant;
import com.get.edgepay.fms.util.CacheUtil;

public class AuthenticationFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

	private static final String MERCHANT_TOKEN = FmsCacheConstant.MERCHANT_TOKEN.name();

	@Autowired
	private CacheUtil cacheUtil;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);
		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.configureBean(this, "cacheUtil");
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		log.info("In AuthenticationFilter...");

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		String authToken = fetchHeaderValueByKey(httpServletRequest, "authToken");
		log.info("Authentication token in request = " + authToken);

		String authTokenInCache = (String) cacheUtil.getFromCache(MERCHANT_TOKEN, MERCHANT_TOKEN, String.class);
		log.info("Authentication token in cache = " + authTokenInCache);

		if (authToken != null && authTokenInCache != null) {
			if (authToken.equalsIgnoreCase(authTokenInCache)) {
				filterChain.doFilter(request, response);
				return;
			}
		}
	}

	private String fetchHeaderValueByKey(HttpServletRequest request, String key) {
		String value = null;
		if (request != null && key != null) {
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerKey = headerNames.nextElement();
				String headerValue = request.getHeader(key);
				if (key.equalsIgnoreCase(headerKey)) {
					value = headerValue;
				}
			}
		}

		return value;
	}

}
