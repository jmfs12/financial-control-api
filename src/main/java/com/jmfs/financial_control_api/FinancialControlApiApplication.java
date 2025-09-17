package com.jmfs.financial_control_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication()
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class FinancialControlApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialControlApiApplication.class, args);
	}

}
