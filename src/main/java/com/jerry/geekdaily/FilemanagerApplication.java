package com.jerry.geekdaily;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class FilemanagerApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(FilemanagerApplication.class, args);
    }

    //Tomcat large file upload connection reset
    @Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                SecurityConstraint securityConstraint=new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection=new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                //-1 means unlimited
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });
        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    /**
     * 让我们的应用支持HTTP是个好想法，但是需要重定向到HTTPS，
     * 但是不能同时在application.properties中同时配置两个connector，
     * 所以要以编程的方式配置HTTP connector，然后重定向到HTTPS connector
     * @return Connector
     */
    public Connector initiateHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //Connector监听的http的端口号
        connector.setPort(80);
        //设置为false 若http请求则重定向到https   设置为true  http请求则为http请求   https为https请求
        connector.setSecure(true);
        //监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(443); // application.properties中配置的https端口
        return connector;
    }



}
