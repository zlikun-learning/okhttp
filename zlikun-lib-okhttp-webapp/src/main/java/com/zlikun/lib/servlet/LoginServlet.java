package com.zlikun.lib.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * 登录控制器
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 12:02
 */
public class LoginServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(LoginServlet.class) ;

    /**
     * 获取登录令牌(模拟登录表单)
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = UUID.randomUUID().toString() ;
        logger.info("访问登录请求，生成Token：{}" ,token);
        req.getSession().setAttribute("token" ,token);
        resp.addHeader("Content-Type" ,"application/json");
        resp.getWriter().printf("{\"token\":\"%s\"}" ,token) ;
    }

    /**
     * 处理登录请求，登录成功后将登录用户写入SESSION，并返回登录用户名
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        resp.addHeader("Content-Type" ,"application/json");
        PrintWriter pw = resp.getWriter() ;

        String username = req.getParameter("username") ;
        String password = req.getParameter("password") ;
        String token = req.getParameter("token") ;

        if (username == null || password == null || token == null) {
            pw.printf("{\"code\":-1,\"message\":\"参数错误\"}") ;
            return ;
        }

        HttpSession session = req.getSession() ;
        String checkToken = (String) session.getAttribute("token");
        if (checkToken == null || !checkToken.equals(token)) {
            pw.printf("{\"code\":-2,\"message\":\"令牌无效\"}") ;
            return ;
        }

        if (!username.equals("zlikun")) {
            pw.printf("{\"code\":-3,\"message\":\"帐号不存在\"}") ;
            return ;
        }

        // 模拟登录，用户名与密码一致即算认证通过
        if (!password.equals(username)) {
            pw.printf("{\"code\":-4,\"message\":\"密码错误\"}") ;
            return ;
        }

        // 将登录用户信息写入SESSION
        session.setAttribute("login_user" ,username);

         pw.printf("{\"code\":1,\"message\":\"登录成功\",\"principal\":\"%s\"}" ,username) ;
    }

}