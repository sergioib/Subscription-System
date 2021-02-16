package com.sigea.emailService.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

	@Bean
	public MessageConverter messageConverter(){
	    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

	    return new Jackson2JsonMessageConverter(mapper);
	}
}
