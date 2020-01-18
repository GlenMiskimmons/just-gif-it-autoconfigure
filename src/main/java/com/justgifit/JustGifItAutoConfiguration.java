package com.justgifit;

import com.justgifit.services.ConverterService;
import com.justgifit.services.GifEncoderService;
import com.justgifit.services.VideoDecoderService;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.FormContentFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@ConditionalOnClass({FFmpegFrameGrabber.class, AnimatedGifEncoder.class})
@EnableConfigurationProperties(JustGifItProperties.class)
public class JustGifItAutoConfiguration {

//    @Value("${multipart.location}/gif")
//    private String gifLocation;

    @Autowired
    private JustGifItProperties properties;

    @ConditionalOnProperty(prefix = "com.justgifit", name = "create-result-dir")
    private Boolean createResultDirectory() {

        if(!properties.getGifLocation().exists()) {
            properties.getGifLocation().mkdir();
        }

        return true;
    }

    @Bean
    @ConditionalOnMissingBean(VideoDecoderService.class)
    public VideoDecoderService videoDecoderService() {
        return new VideoDecoderService();
    }

    @Bean
    @ConditionalOnMissingBean(GifEncoderService.class)
    public GifEncoderService gifEncoderService() {
        return new GifEncoderService();
    }

    @Bean
    @ConditionalOnMissingBean(ConverterService.class)
    public ConverterService converterService() {
        return new ConverterService();
    }

    @Configuration
    @ConditionalOnWebApplication
    public static class WebConfiguration {

        @Value("${multipart.location}/gif")
        private String gifLocation;

        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addResourceHandlers(ResourceHandlerRegistry registry) {
                    registry.addResourceHandler("/gif/**")
                            .addResourceLocations("file:" + gifLocation);
                }
            };
        }

//        @Bean
//        @ConditionalOnProperty(prefix = "com.justgifit", name="optimize")
//        public FilterRegistrationBean<HiddenHttpMethodFilter> deRegisterHiddenHttpMethodFilter (HiddenHttpMethodFilter filter) {
//            FilterRegistrationBean<HiddenHttpMethodFilter> bean = new FilterRegistrationBean<>(filter);
//            bean.setEnabled(false);
//
//            return bean;
//        }

        @Bean
        @ConditionalOnProperty(prefix = "com.justgifit", name="optimize")
        public FilterRegistrationBean<FormContentFilter> deRegisterFormContentFilter(FormContentFilter filter) {
            FilterRegistrationBean<FormContentFilter> bean = new FilterRegistrationBean<>(filter);
            bean.setEnabled(false);

            return bean;
        }

//        @Bean
//        @ConditionalOnProperty(prefix = "com.justgifit", name="optimize")
//        public FilterRegistrationBean<RequestContextFilter> deRegisterRequestContextFilter(RequestContextFilter filter) {
//            FilterRegistrationBean<RequestContextFilter> bean = new FilterRegistrationBean<>(filter);
//            bean.setEnabled(false);
//
//            return bean;
//        }

    }

}
