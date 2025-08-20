package com.jmfs.financial_control_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class FinancialControlApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialControlApiApplication.class, args);
	}

}
