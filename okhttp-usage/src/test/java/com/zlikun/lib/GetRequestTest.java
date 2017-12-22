package com.zlikun.lib;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP协议GET请求测试用例
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 9:00
 */
public class GetRequestTest {

    private Logger logger = LoggerFactory.getLogger(GetRequestTest.class) ;
    private OkHttpClient client = new OkHttpClient();

    @Test
    public void request() {

        String url = "http://localhost/hello" ;
        String query = "message=OkHttp" ;

        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(30 , TimeUnit.SECONDS)
//                .noCache()
                .build() ;

        // 构建GET请求
        Request request = new Request.Builder()
                .addHeader("User-Agent" ,"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .url(String.format("%s?%s" ,url ,query))
                .cacheControl(cacheControl)
                .get()
                .build() ;

        Response response = null ;
        try {
            // 执行请求，并返回响应对象
            response = client.newCall(request).execute() ;
        } catch (IOException e) {
            logger.error("执行GET请求出错!" ,e);
            Assert.fail(e.getMessage());
        }

        // 输出响应状态
        logger.info("status = {} ,message = {}" ,response.code() ,response.message());

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