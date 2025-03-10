//package com.arbitragebroker.client.config;
//
//import com.arbitragebroker.client.exception.ErrorResponse;
//import com.arbitragebroker.client.util.CustomObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//@Component
//public class CustomAccessDeniedHandler implements AccessDeniedHandler {
//
//    @Override
//    public void handle(HttpServletRequest request,
//                       HttpServletResponse response,
//                       AccessDeniedException accessDeniedException) throws IOException {
//        // Check the request type: if it accepts JSON, return a JSON response
//        if ("application/json".equals(request.getHeader("Accept"))) {
//            response.setStatus(HttpStatus.FORBIDDEN.value());
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            PrintWriter out = response.getWriter();
//            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, request.getRequestURI(),
//                    "Access Denied: " + accessDeniedException.getMessage());
//            out.print(new CustomObjectMapper().writeValueAsString(errorResponse));
//            out.flush();
//        } else {
//            // If it's a web request, redirect to the access denied page
////            response.sendRedirect("/page_403");
//            response.sendRedirect("/page_403");
//        }
//    }
//}