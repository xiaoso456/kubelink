package io.github.xiaoso456.kubelink.config;


import io.github.xiaoso456.kubelink.domain.KubeLinkProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KubeLinkAutoConfig {

    @Bean
    public KubeLinkProperties kubeLinkProperties(){
        return new KubeLinkProperties();
    }

}
