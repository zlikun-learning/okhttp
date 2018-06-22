package com.zlikun.learning.oschina;

import com.zlikun.learning.MemoryCookieJar;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * 开源中国登录测试
 */
@Slf4j
public class OSChinaLoginTest {

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(3000, TimeUnit.MILLISECONDS)
            .readTimeout(1500, TimeUnit.MILLISECONDS)
            .writeTimeout(3000, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .addNetworkInterceptor(chain -> {
                Request request = chain.request();
                log.info("[Interceptor] request url : {}", request.url().toString());
                request.headers().toMultimap().entrySet()
                        .stream()
                        .forEach(entry -> {
                            log.info("[Interceptor] header-name = {}, header-value = {}", entry.getKey(), entry.getValue().get(0));
                        });
                return chain.proceed(request);
            })
            .cookieJar(new MemoryCookieJar())
            .build();

    private String password = "";

    @Test
    public void login() throws NoSuchAlgorithmException, IOException {

        // 模拟表单登录
        Request request = new Request.Builder()
                .url("https://www.oschina.net/action/user/hash_login?from=")
                .post(new FormBody.Builder()
                        .add("email", "likun_zhang@yeah.net")
                        .add("pwd", sha1(password))
                        .add("verifyCode", "")
                        .add("save_login", "1")
                        .build())
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .build();

        // 执行模拟登录请求
        Response response = client.newCall(request).execute();

        // response status
        if (!response.isSuccessful()) {
            log.info("code = {}, message = {}", response.code(), response.message());
            return;
        }

        // response body
        log.info("body = {}", response.body().string());

//        // response headers
//        response.headers().toMultimap().entrySet()
//                .stream()
//                .forEach(entry -> {
//                    log.info("header-name = {}, header-value = {}", entry.getKey(), entry.getValue().get(0));
//                });

        // 登录完成后，需要保持登录会话ID，并访问首页，从首页上获取登录用户信息
        access("http://www.oschina.net/");

        // 其它操作，如遍历用户博客信息等

    }

    private void access(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        // response status
        if (!response.isSuccessful()) {
            log.info("code = {}, message = {}", response.code(), response.message());
            return;
        }

        // parse page
        Document doc = Jsoup.parse(response.body().string(), url);
        String username = doc.select("header.header-navbar div.box.user-info > span.name").text();
        log.info("login user : {}", username);
    }

    @Test
    public void sha1() throws NoSuchAlgorithmException {
        assertEquals("3d4f2bf07dc1be38b20cd6e46949a1071f9d0e3d", sha1("111111"));
    }

    /**
     * SHA-1加密
     *
     * @param text
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String sha1(String text) throws NoSuchAlgorithmException {
        if (text == null) throw new IllegalArgumentException();
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(text.getBytes());
        return hexText(digest.digest());
    }

    private String hexText(byte[] bytes) {
        final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        final int len = bytes.length;
        final StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

}
