package com.zlikun.lib;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HTTP协议POST请求测试用例
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 9:18
 */
public class PostRequestTest {

    private Logger logger = LoggerFactory.getLogger(PostRequestTest.class) ;

    private OkHttpClient client = new OkHttpClient();
    private MediaType contentType = MediaType.parse("application/x-www-form-urlencoded");

    @Test
    public void request() {

        String url = "http://localhost/hello" ;

        // 构建GET请求
        Request request = new Request.Builder()
                .addHeader("User-Agent" ,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .url(url)
                .post(RequestBody.create(contentType,"message=OkHttp"))
                .build() ;

        Response response = null ;
        try {
            // 执行请求，并返回响应对象
            response = client.newCall(request).execute() ;
        } catch (IOException e) {
            logger.error("执行GET请求出错!" ,e);
            Assert.fail(e.getMessage());
        }

        // 输出响应信息
        if (response.isSuccessful()) {
            try {
                logger.info("\n\n{}\n\n" ,response.body().string());
            } catch (IOException e) {
                logger.error("获取响应结果出错!" ,e);
                Assert.fail(e.getMessage());
            }
        }

    }

}