package com.zlikun.lib.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 受保护资源请求控制器，只有登录用户才能访问
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 12:16
 */
public class ResourceServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(ResourceServlet.class) ;

    /**
     * 模拟受保护的资源，只有登录用户才能访问，否则返回401
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession() ;

        String loginUser = (String) session.getAttribute("login_user");

        // 检查是否认证
        if (loginUser == null) {
            logger.warn("用户未认证");
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return ;
        }

        // 检查是否授权(模拟权限，只有用户名为zlikun的认证用户有权限访问资源)
        if (!loginUser.equals("zlikun")) {
            logger.warn("用户[{}]请求未授权" ,loginUser);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ;
        }

        String name = req.getParameter("name") ;

        resp.getWriter().printf("{\"user\":\"%s\",\"resource\":\"%s\"}" ,name) ;

    }
}