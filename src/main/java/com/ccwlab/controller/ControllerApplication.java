package com.ccwlab.controller;

import com.ccwlab.controller.message.MyProcessor;
import com.ccwlab.controller.message.WorkerProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding({MyProcessor.class, WorkerProcessor.class})
public class ControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControllerApplication.class, args);
	}

}
