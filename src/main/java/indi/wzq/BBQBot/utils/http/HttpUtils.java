package indi.wzq.BBQBot.utils.http;

import indi.wzq.BBQBot.enums.HttpCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpUtils {

    private static final X509TrustManager manager = HttpUtils.getX509TrustManager();
    private static final OkHttpClient client = new OkHttpClient().newBuilder()
            //调用超时
            .callTimeout(30, TimeUnit.SECONDS)
            //链接超时
            .connectTimeout(30, TimeUnit.SECONDS)
            //读取超时
            .readTimeout(30, TimeUnit.SECONDS)
            //忽略SSL校验
            .sslSocketFactory(HttpUtils.getSocketFactory(manager), manager)
            //忽略校验
            .hostnameVerifier(HttpUtils.getHostnameVerifier())
            //连接池
            .connectionPool(new ConnectionPool(10, 20, TimeUnit.MINUTES))
            .build();

    public static Body sendGet(String url) {
        return sendGet(url ,
                "" ,
                Headers.of("*", "*").newBuilder()
        );
    }
    public static Body sendGet(String url, String param) {
        return sendGet(
                url ,
                param ,
                Headers.of("*", "*").newBuilder()
        );
    }
    /**
     * 发送 GET 请求
     * @param url 地址
     * @param param 参数
     * @param headers 请求头
     * @return 返回体
     */
    public static Body sendGet(String url, String param, Headers.Builder headers){
        try (
                Response response = client.newCall(send(url, param, headers)).execute()
        ) {

            //返回体
            return getBody(response);

        } catch (IOException e) {
            log.error(e.getMessage());
            return new Body(HttpCodeEnum.ERROR);
        }
    }


    public static Body sendGetFile(String url){
        return sendGetFile(
                url ,
                "" ,
                Headers.of("*", "*").newBuilder()
        );
    }
    public static Body sendGetFile(String url, String param){
        return sendGetFile(
                url ,
                param ,
                Headers.of("*", "*").newBuilder()
        );
    }
    /**
     * 发送获取文件的 GET 请求
     * @param url 地址
     * @param param 参数
     * @param headers 请求头
     * @return 返回体
     */
    public static Body sendGetFile(String url, String param, Headers.Builder headers){
        try (
                Response response = client.newCall(send(url, param, headers)).execute()
        ) {
            //返回体
            return getFileBody(response);

        } catch (IOException e) {
            log.error(e.getMessage());
            return new Body(HttpCodeEnum.ERROR);
        }
    }

    /**
     * 构造请求体
     * @param url 地址
     * @param param 参数
     * @param headers 请求头
     * @return 请求体
     */
    private static Request send(String url, String param, Headers.Builder headers) {
        String urlNameString;
        if (!param.isEmpty()) {
            urlNameString = url + "?" + param;
        } else {
            urlNameString = url;
        }

        if (headers == null) {
            return new Request.Builder()
                    .url(urlNameString)
                    .get()
                    .build();

        }

        return new Request.Builder()
                .url(urlNameString)
                .get()
                .headers(headers.build())
                .build();

    }


    @NotNull
    private static Body getBody(Response response) {
        Body body = new Body();
        Optional.ofNullable(response.body()).ifPresentOrElse(r -> {
            //响应体
            try {
                body.setBody(r.string());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //响应code
            body.setCode(HttpCodeEnum.getCode(response.code()));
            //响应头
            body.setHeaders(response.headers());

            response.close();
        }, () -> body.setCode(HttpCodeEnum.getCode(response.code())));
        response.close();
        return body;
    }
    @NotNull
    private static Body getFileBody(Response response) {
        Body body = new Body();
        Optional.ofNullable(response.body()).ifPresentOrElse(r -> {
            //响应体
            try {
                body.setFile(r.bytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //响应code
            body.setCode(HttpCodeEnum.getCode(response.code()));
            //响应头
            body.setHeaders(response.headers());

            response.close();
        }, () -> body.setCode(HttpCodeEnum.getCode(response.code())));
        response.close();
        return body;
    }


    private static SSLSocketFactory getSocketFactory(TrustManager manager) {
        SSLSocketFactory factory = null;
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[]{manager}, new SecureRandom());
            factory = context.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return factory;
    }

    public static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    private static HostnameVerifier getHostnameVerifier() {
        return (s, sslSession) -> true;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        String body;
        byte[] file;
        HttpCodeEnum code;
        Headers headers;

        public Body(HttpCodeEnum code) {
            this.code = code;
        }
    }

}
