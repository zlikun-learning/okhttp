package com.zlikun.lib.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 9:29
 */
public class HelloServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(HelloServlet.class) ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Principal principal = req.getUserPrincipal() ;
        if (principal != null) {
            logger.info("principal = {}" ,principal);
        }

        logger.info("User-Agent = {}" ,req.getHeader("User-Agent"));
        logger.info("Authorization = {}" ,req.getHeader("Authorization"));

        String message = req.getParameter("message") ;
        if (message == null) {
            message = "Jetty" ;
        }
        resp.getWriter().printf("Hello %s" ,message);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req ,resp);
    }
}