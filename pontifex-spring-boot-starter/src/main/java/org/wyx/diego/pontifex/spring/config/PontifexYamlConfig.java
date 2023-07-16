package org.wyx.diego.pontifex.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:pontifex.yml")
public class PontifexYamlConfig {


    //alarm
    @Value("${pontifex.alarm.feiShu.url}")
    private String feiShuUrl;


    public String getFeiShuUrl() {
        return feiShuUrl;
    }

    public PontifexYamlConfig setFeiShuUrl(String feiShuUrl) {
        this.feiShuUrl = feiShuUrl;
        return this;
    }
}
