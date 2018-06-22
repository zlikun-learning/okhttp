package com.zlikun.learning.tmp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * 尝试暴力破解登录API
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017-12-22 19:47
 */
@Slf4j
public class LoginApiTest {

    private OkHttpClient client = new OkHttpClient();
    private MediaType contentType = MediaType.parse("application/x-www-form-urlencoded");

    @Test
    public void login() {

        request("15618950163", "123456");

    }

    /**
     * 登录请求
     * @param account
     * @param password
     */
    private void request(String account, String password) {

        String url = "不告诉你^_^" ;

        // 构建GET请求
        Request request = new Request.Builder()
                .addHeader("User-Agent" ,"OkHttp3")
                .url(url)
                .post(RequestBody.create(contentType,
                        "account=" + account + "&password=" + password + "&areaCode=86&appVersion=3.0.7&clientType=1&imei=$imei"))
                .build() ;

        Response response = null ;
        try {
            // 执行请求，并返回响应对象
            response = client.newCall(request).execute() ;
        } catch (IOException e) {
            log.error("执行POST请求出错!" ,e);
            Assert.fail(e.getMessage());
        }

        // 输出响应信息
        if (response.isSuccessful()) {
            try {
                log.info("{}" ,response.body().string());
            } catch (IOException e) {
                log.error("获取响应结果出错!" ,e);
            }
        } else {
            log.info("login error, code = {}, message = {}, args = [ {}, {} ]", response.code(), response.message(), account, password);
        }

    }

}
