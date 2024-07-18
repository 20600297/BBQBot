package indi.wzq.BBQBot.config;

import io.undertow.server.handlers.DisallowedMethodsHandler;
import io.undertow.util.HttpString;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * Undertow的配置文件
 * 拦截 CONNECT TRACE TRACK 请求
 * 禁止外部尝试用服务做代理
 */
@Configuration
public class UndertowWebServerCustomizerConfig implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo.addInitialHandlerChainWrapper(handler -> {

            //禁止三个方法TRACE也是不安全的
            System.out.println("disable HTTP methods: CONNECT/TRACE/TRACK");
            HttpString[] disallowedHttpMethods = {
                    HttpString.tryFromString("CONNECT"),
                    HttpString.tryFromString("TRACE"),
                    HttpString.tryFromString("TRACK")
            };
            return new DisallowedMethodsHandler(handler, disallowedHttpMethods);
        }));
    }
}