package com.foodtech.ms_factura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class MsFacturaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsFacturaApplication.class, args);
	}

}
