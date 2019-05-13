package by.litelife.mk.pollutanttransport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PollutantTransportApplication {

    public static void main(String[] args) {
        SpringApplication.run(PollutantTransportApplication.class, args);
    }

}
