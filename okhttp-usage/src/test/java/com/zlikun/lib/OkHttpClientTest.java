package com.zlikun.lib;

import okhttp3.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * OkHttpClient 构建过程测试用例
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 9:45
 */
public class OkHttpClientTest {

    private Logger logger = LoggerFactory.getLogger(OkHttpClientTest.class);

    @Test
    public void build() {

        // 构建连接池
        ConnectionPool pool = new ConnectionPool(30, 1000, TimeUnit.MILLISECONDS);

        // https://github.com/square/okhttp/wiki/HTTPS
        ConnectionSpec httpSpec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build();
        ConnectionSpec httpsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                ).build();

        // https://github.com/square/okhttp/wiki/HTTPS#certificate-pinning
        // https://github.com/square/okhttp/wiki/HTTPS#customizing-trusted-certificates
        CertificatePinner pinner = new CertificatePinner.Builder().build();

        // https://github.com/square/okhttp/wiki/Recipes#handling-authentication
        Authenticator authenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic("zlikun", "xxxxxx");
                return response.request().newBuilder()
                        .header("Authorization", credential)
                        .build();
            }
        } ;

        // https://github.com/square/okhttp/wiki/Interceptors
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                long begin = System.nanoTime();
                logger.info(String.format("Sending request %s on %s%n%s",
                        request.url(), chain.connection(), request.headers()));

                Response response = chain.proceed(request);

                long end = System.nanoTime();
                logger.info(String.format("Received response for %s in %.1fms%n%s",
                        response.request().url(), (end - begin) / 1e6d, response.headers()));

                return response;
            }
        } ;

        // 构造 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .connectTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .connectionSpecs(Arrays.asList(httpSpec, httpsSpec))
                .certificatePinner(pinner)
                .authenticator(authenticator)
                .cache(new Cache(new java.io.File(System.getProperty("java.io.tmpdir")) ,10 * 1024 * 1024))
                .dns(new Dns() {
                    @Override
                    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                        logger.info("hostname = {}" ,hostname);
                        if (hostname == null) throw new UnknownHostException("hostname == null");
                        // 将 www.zlikun.com 指向 localhost
                        if (hostname.equals("www.zlikun.com")) {
                            return Arrays.asList(InetAddress.getByName("localhost")) ;
                        }
                        // 使用系统DNS
                        return Dns.SYSTEM.lookup(hostname);
                    }
                })
                .addInterceptor(interceptor)
                .addNetworkInterceptor(interceptor)
                .build();

        get(client, "http://www.zlikun.com/hello", "message=OkHttp");

    }

    /**
     * GET请求
     *
     * @param client
     * @param url
     * @param query
     */
    private void get(OkHttpClient client, String url, String query) {

        // 构建GET请求
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .url(String.format("%s?%s", url, query))
                .get()
                .build();

        Response response = null;
        try {
            // 执行请求，并返回响应对象
            response = client.newCall(request).execute();
        } catch (IOException e) {
            logger.error("执行GET请求出错!", e);
            Assert.fail(e.getMessage());
        }

        // 输出响应信息
        if (response.isSuccessful()) {
            try {
                logger.info("\n\n{}\n\n", response.body().string());
            } catch (IOException e) {
                logger.error("获取响应结果出错!", e);
                Assert.fail(e.getMessage());
            }
        }
    }

}