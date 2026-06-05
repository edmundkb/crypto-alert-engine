package com.crypto.alert.engine.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "market.data";
    public static final String QUEUE_NAME = "alert-engine-queue";
    public static final String ROUTING_KEY_PATTERN = "market.data.#";

    @Bean
    public Queue alertEngineQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public TopicExchange marketDataExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Binding bindEngineToMarketData(Queue alertEngineQueue, TopicExchange marketDataExchange) {
        return BindingBuilder.bind(alertEngineQueue)
                .to(marketDataExchange)
                .with(ROUTING_KEY_PATTERN);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}