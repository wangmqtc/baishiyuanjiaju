package com.baishiyuan.partOne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
		"com.baishiyuan.*"
})
//@SpringBootApplication(exclude = {MongoAutoConfiguration.class,MongoDataAutoConfiguration.class})
@SpringBootApplication
public class PartOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartOneApplication.class, args);
	}

}
