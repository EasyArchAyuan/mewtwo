package com.ayuan.mewtwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MewtwoApplication {

    public static void main(String[] args) {
        MewtwoHttpServer httpServer = new MewtwoHttpServer();
        httpServer.start();
        SpringApplication.run(MewtwoApplication.class, args);
    }
}
