package com.zlikun.learning.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * 重定向请求控制器
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 12:33
 */
public class RedirectServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(RedirectServlet.class) ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String target = req.getParameter("target") ;
        // 检查target参数
        if (target == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return ;
        }
        // 检查target参数是否为标准URL
        URI uri = null;
        try {
            uri = URI.create(target);
        } catch (IllegalArgumentException e) {
            logger.error("指定的target参数不合法!" ,e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return ;
        }
        // 重定向的URL与当前URL一致，禁止访问
        if (uri.getPath() != null && uri.getPath().equals(req.getRequestURI())) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return ;
        }
        // 执行重定向
        resp.sendRedirect(target);
    }
}