package core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RequestProcessingTimeInterceptor.class);
 
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	long time = System.currentTimeMillis();
    	request.setAttribute("time", time);
    	return true;
    }
     
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    	long totalTime = System.currentTimeMillis() - (Long)request.getAttribute("time");
    	logger.info("request: {}, totalTime passed: {} ms", handler, totalTime);
	}
}
