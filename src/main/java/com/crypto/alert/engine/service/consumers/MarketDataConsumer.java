package com.crypto.alert.engine.service.consumers;

import com.crypto.alert.engine.config.RabbitConfig;
import com.crypto.alert.engine.model.TickerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MarketDataConsumer {

    private static final Logger log = LoggerFactory.getLogger(MarketDataConsumer.class);
    private static final double BTC_ALERT_THRESHOLD = 60982.51;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeMarketTicks(TickerRecord tick) {
        Flux.just(tick)
                .filter(t -> "BTC-USD".equalsIgnoreCase(t.productId()))
                .map(t -> Double.parseDouble(t.price()))
                .filter(price -> price > BTC_ALERT_THRESHOLD)
                .subscribe(
                        highPrice -> log.warn("ALERT! Bitcoin has breached threshold: ${}", highPrice),
                        error -> log.error("Error encountered in processing stream", error)
                );
    }
}