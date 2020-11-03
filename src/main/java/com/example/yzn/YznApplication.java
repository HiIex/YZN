package com.example.yzn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootApplication
@MapperScan("com.example.yzn")
public class YznApplication {

	public static void main(String[] args) {
		SpringApplication.run(YznApplication.class, args);
	}

	@Component
	@Order(1)
	public class AESKeyRunner implements CommandLineRunner {
		@Override
		public void run(String... args) throws Exception {
			System.out.println("The OrderRunner1 start to initialize ...");
		}
	}

}
