package com.zlikun.lib;

import com.zlikun.lib.servlet.HelloServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.ServletHandler;

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

        // Set a handler
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        handler.addServletWithMapping(HelloServlet.class, "/hello");

        // Start the server
        server.dumpStdErr();
        server.start();
        server.join();

    }

}