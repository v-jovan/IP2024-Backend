package org.unibl.etf.ip2024;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Ip2024Application {

	public static void main(String[] args) {
		SpringApplication.run(Ip2024Application.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setAmbiguityIgnored(true);
		return mapper;
	}

}
