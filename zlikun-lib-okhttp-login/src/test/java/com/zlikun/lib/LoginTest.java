package com.zlikun.lib;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/15 9:12
 */
public class LoginTest {

    // 构造 OkHttpClient 实例
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(1000, TimeUnit.MILLISECONDS)
            .writeTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .cookieJar(new CookieJar() {

                // 使用ConcurrentMap存储cookie信息，因为数据在内存中，所以只在程序运行阶段有效，程序结束后即清空
                private ConcurrentMap<String ,List<Cookie>> storage = new ConcurrentHashMap<String ,List<Cookie>>() ;

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    String host = url.host() ;
                    storage.put(host ,cookies) ;
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    String host = url.host() ;
                    return storage.get(host) ;
                }
            })
            .build();

    /**
     * 获取登录Token
     * @return
     * @throws IOException
     */
    private String loginToken() throws IOException {
        // 构建登录表单请求，获取Token
        Request request = new Request.Builder()
                .url("http://localhost/login")
                .get()
                .build() ;

        Response response = client.newCall(request).execute() ;

        if (response.isSuccessful()) {

            // TODO 获取Token信息

        }

        return null ;
    }

    /**
     * 访问请求资源(资源受登录保护)
     */
    private void accessResource() {

    }

    @Test
    public void login() throws IOException {

        // 获取令牌
        final String token = loginToken() ;
        Assert.assertNotNull(token);

        // 构建表单消息体
        FormBody body = new FormBody.Builder()
                .add("username" ,"zlikun")
                .add("password" ,"zlikun")
                .build() ;

        // 构建登录请求
        Request request = new Request.Builder()
                .url("http://localhost/login")
                .post(body)
                .build() ;

        Response response = client.newCall(request).execute() ;

        if (response.isSuccessful()) {

            // TODO 登录成功处理


            // 请求受保护资源
            accessResource() ;
        }

    }

}
