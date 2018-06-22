package com.zlikun.learning;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/15 9:12
 */
public class LoginTest {

    private Logger logger = LoggerFactory.getLogger(LoginTest.class);

    // 构造 OkHttpClient 实例
    private OkHttpClient client;

    @Before
    public void init() {

        client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .cookieJar(new MemoryCookieJar())
                .build();

    }

    /**
     * 获取登录Token
     *
     * @return
     * @throws IOException
     */
    private String loginToken() throws IOException {
        // 构建登录表单请求，获取Token
        Request request = new Request.Builder()
                .url("http://localhost/login")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        String message = null;

        if (response.isSuccessful()) {

            // 获取Token信息
            message = response.body().string();

        }

        return message;
    }

    /**
     * 访问请求资源(资源受登录保护)
     */
    private void accessResource() throws IOException {
        // 构建登录表单请求，获取Token
        Request request = new Request.Builder()
                .url("http://localhost/resource?name=xxx")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            logger.info("获取资源信息：{}" ,response.body().string());
        } else {
            logger.info("code = {} ,message = {}" ,response.code() ,response.message());
        }
    }

    @Test
    public void login() throws IOException {

        // 获取令牌
        final String token = loginToken();
        Assert.assertNotNull(token);
        logger.info("Login Token : {}" ,token);

        // 构建表单消息体
        FormBody body = new FormBody.Builder()
                .add("token" ,token)
                .add("username" ,"zlikun")
                .add("password" ,"zlikun")
//                .add("username" ,"kevin")
//                .add("password" ,"kevin")
                .build() ;

        // 构建登录请求
        Request request = new Request.Builder()
                .url("http://localhost/login")
                .post(body)
                .build() ;

        Response response = client.newCall(request).execute() ;

        if (response.isSuccessful()) {

            // 登录成功处理
            logger.info("Login Response : {}" ,response.body().string());

            // 请求受保护资源
            accessResource() ;
        }

    }

}
