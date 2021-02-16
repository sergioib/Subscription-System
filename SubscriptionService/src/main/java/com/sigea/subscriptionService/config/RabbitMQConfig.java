package com.sigea.subscriptionService.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitMQConfig {

	@Value("${subscriptionService.rabbitmq.queue}")
	String queueName;

	
	@Bean
	Queue queue() {
		return new Queue(queueName, true);
	}

	@Bean
	public MessageConverter messageConverter(){
	    ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

	    return new Jackson2JsonMessageConverter(mapper);
	}
}
