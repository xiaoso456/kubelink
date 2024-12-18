package io.github.xiaoso456.kubelink;

import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
@Slf4j
@MapperScan("io.github.xiaoso456.kubelink.*")
@EnableScheduling
public class KubeLinkApplication implements CommandLineRunner {

    @Value("${server.port:15151}")
    private String port;


    public static void main(String[] args) throws IOException, URISyntaxException {
        SpringApplication.run(KubeLinkApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Started {} in http://localhost:{}",KubeLinkApplication.class.getSimpleName(),port);
    }
}
