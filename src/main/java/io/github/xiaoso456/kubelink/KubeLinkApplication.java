package io.github.xiaoso456.kubelink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan
public class KubeLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KubeLinkApplication.class, args);
    }

}
