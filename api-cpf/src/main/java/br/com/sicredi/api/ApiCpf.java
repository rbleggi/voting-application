package br.com.sicredi.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class ApiCpf {

    public static void main(String[] args) {
        SpringApplication.run(ApiCpf.class, args);
    }

}