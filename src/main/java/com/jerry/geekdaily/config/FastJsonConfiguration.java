package com.jerry.geekdaily.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
* @Description:    配置json返回视图
* @Author:         liulei
* @CreateDate:     2018/7/7 9:19
* 注意：WebMvcConfigurerAdapter（老版本  已过时）必须要用WebMvcConfigurationSupport替换   不然配置不会生效
*/
@Configuration
public class FastJsonConfiguration extends WebMvcConfigurerAdapter
{
//    /**
//     * 方式一   修改自定义消息转换器  用WebMvcConfigurationSupport会报一些错误（坑） 如：图片路径等加载出问题
//     * 实体类中使用@JSONField(serialize=false)，此字段就不返回了
//     * @param converters 消息转换器列表
//     */
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //调用父类的配置
//        super.configureMessageConverters(converters);
//        //创建fastJson消息转换器
//        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
//        //创建配置类
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
////        FastJson SerializerFeatures
////        PrettyFormat:结果是否格式化,默认为false
////        SortField:按字段名称排序后输出。默认为false
////        WriteNullListAsEmpty  ：List字段如果为null,输出为[],而非null
////        WriteNullStringAsEmpty ： 字符类型字段如果为null,输出为"",而非null
////        DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
////        WriteNullBooleanAsFalse：Boolean字段如果为null,输出为false,而非null
////        WriteNullNumberAsZero:数值字段如果为null,输出为0,而非null
////        WriteNullBooleanAsFalse:数值字段如果为null,输出为0,而非null
////        WriteMapNullValue：是否输出值为null的字段,默认为false。
//        //修改配置返回内容的过滤
//        fastJsonConfig.setSerializerFeatures(
//                SerializerFeature.PrettyFormat,
////                SerializerFeature.SortField,
//                SerializerFeature.DisableCircularReferenceDetect,
//                SerializerFeature.WriteNullListAsEmpty,
//                SerializerFeature.WriteNullStringAsEmpty,
//                SerializerFeature.WriteNullNumberAsZero
//        );
//        //处理中文乱码问题
//        List<MediaType> fastMediaTypes = new ArrayList<>();
//        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//        fastConverter.setSupportedMediaTypes(fastMediaTypes);
//        fastConverter.setFastJsonConfig(fastJsonConfig);
//        //将fastjson添加到视图消息转换器列表内
//        converters.add(fastConverter);
//    }

    /* 方式二：注入Bean : HttpMessageConverters，以支持fastjson*/
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConvert = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.SortField,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty
        );
        //附加：处理中文乱码（后期添加）
        List<MediaType> fastMedisTypes=new ArrayList<MediaType>();
        fastMedisTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConvert.setSupportedMediaTypes(fastMedisTypes);
        //3、在convert中添加配置信息
        fastConvert.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters((HttpMessageConverter<?>) fastConvert);
    }

}
