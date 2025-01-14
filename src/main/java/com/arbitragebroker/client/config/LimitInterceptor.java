package com.arbitragebroker.client.config;

import com.arbitragebroker.client.exception.MaximumRequestPerMinuteExceededException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LimitInterceptor implements HandlerInterceptor {

    private HashMap<String, List<Date>> requests = new HashMap<>();

    @Override
    public synchronized boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod;
        Method method;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
            method = handlerMethod.getMethod();
        } else {
            return true;
        }
        if (method.isAnnotationPresent(Limited.class) ||
                method.getDeclaringClass().isAnnotationPresent(Limited.class)) {

            Limited limitedData = method.isAnnotationPresent(Limited.class) ?
                    method.getAnnotation(Limited.class) :
                    method.getDeclaringClass().getAnnotation(Limited.class);

            String ipAddress = getClientIp(request);
            String params = Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(","));
            String key = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(" + params + ")-" + ipAddress;

            if (!requests.containsKey(key)) {
                requests.put(key, new ArrayList<>());
            }
            List<Date> list = requests.get(key);
            list.add(new Date());

            long now = new Date().getTime();
            Date start = new Date(now - now % 60000);
            long count = 0;
            try {
                count = list.stream().filter(x -> x.after(start)).count();
            } catch (NullPointerException e){
                // do nothing
            }


            list.removeAll(list.stream().filter(x -> x.before(start)).collect(Collectors.toList()));

            if (count > limitedData.requestsPerMinutes()) {
                throw new MaximumRequestPerMinuteExceededException();
            }
        }

        return true;
    }
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

