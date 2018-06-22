package com.zlikun.learning;

import okhttp3.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * HTTPS请求测试用例
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/5/13 10:50
 */
public class HTTPSTest {

    private Logger logger = LoggerFactory.getLogger(HTTPSTest.class);

    private String domain = "https://tuchong.com" ;
    private String path = "/" ;

    /**
     * 获取X509TrustManager，配置证书
     * @return
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private X509TrustManager getX509TrustManager() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        // 配置SSL/TLS证书
        CertificateFactory cf = CertificateFactory.getInstance("X.509") ;
        Certificate cert = cf.generateCertificate(HTTPSTest.class.getClassLoader().getResourceAsStream("-.tuchong.com.crt")) ;

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()) ;
        keyStore.load(null, null);
        keyStore.setCertificateEntry("*.tuchong.com", cert);

        // TrustManagerFactory.getDefaultAlgorithm() = "PKIX"
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    @Test
    public void request() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {

        // https://github.com/square/okhttp/wiki/HTTPS
        ConnectionSpec httpSpec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT).build();
        ConnectionSpec httpsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0 ,TlsVersion.TLS_1_1 ,TlsVersion.TLS_1_2 ,TlsVersion.TLS_1_3)
                .build();

        // https://github.com/square/okhttp/wiki/HTTPS#certificate-pinning
        // https://github.com/square/okhttp/wiki/HTTPS#customizing-trusted-certificates
        // http://square.github.io/okhttp/3.x/okhttp/okhttp3/CertificatePinner.html
        // 证书锁定，通过add()方法添加信任的授权机构，在没有服务器TLS 管理员允许的情况下，不要使用证书锁定
        CertificatePinner pinner = new CertificatePinner.Builder()
                .build();

        X509TrustManager trustManager = getX509TrustManager() ;

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] {trustManager}, new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // 构造 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(httpSpec, httpsSpec))
                .certificatePinner(pinner)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        logger.info("hostname = [{} ,{}]" ,hostname ,session.getPeerHost());
                        return session != null && session.getPeerHost() != null && hostname != null && session.getPeerHost().equals(hostname);
                    }
                }).build();

        request(client ,domain ,path);

    }

    @Test
    public void trust_all_certificate() throws NoSuchAlgorithmException, KeyManagementException, IOException {

        X509TrustManager trustManager = new X509TrustManager () {

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } ;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] {trustManager}, new SecureRandom());
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // 构造 OkHttpClient 实例
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true ;
                    }
                }).build();

        request(client ,domain ,path);

    }

    private void request(OkHttpClient client ,String domain ,String path) throws IOException {
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")
                .url(domain + path)
                .build();

        Response response = client.newCall(request).execute();

        String text = null ;
        if(response.isSuccessful()) {
            text = response.body().string();
            logger.info("输出下载资源文件[{}]正文：\n\n{}\n\n" ,path ,text);
        }

        response.close();
    }

}