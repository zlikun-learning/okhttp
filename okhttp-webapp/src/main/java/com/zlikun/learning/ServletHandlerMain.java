package com.zlikun.learning;

import com.zlikun.learning.servlet.HelloServlet;
import com.zlikun.learning.servlet.LoginServlet;
import com.zlikun.learning.servlet.RedirectServlet;
import com.zlikun.learning.servlet.ResourceServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Jetty实现HTTP请求(基于Servlet实现)类
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 9:27
 */
public class ServletHandlerMain {

    public static void main(String[] args) throws Exception {

        // The Server
        Server server = new Server();
        server.setStopAtShutdown(true);
        server.setSessionIdManager(new HashSessionIdManager());

        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(80);
        http.setStopTimeout(30000);
        http.setIdleTimeout(30000);
        server.addConnector(http);

        // set servlet context
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // add servlets
        context.addServlet(HelloServlet.class, "/hello");
        context.addServlet(LoginServlet.class, "/login");
        context.addServlet(ResourceServlet.class, "/resource");
        context.addServlet(RedirectServlet.class, "/link");

        // Start the server
        server.dumpStdErr();
        server.start();
        server.join();

    }

}