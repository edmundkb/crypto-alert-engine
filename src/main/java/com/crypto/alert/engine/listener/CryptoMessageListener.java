package com.crypto.alert.engine.listener;

import com.crypto.alert.engine.entity.CryptoPrice;
import com.crypto.alert.engine.repository.CryptoPriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CryptoMessageListener {

    private static final Logger log = LoggerFactory.getLogger(CryptoMessageListener.class);

    private final CryptoPriceRepository priceRepository;

    //Thread-safe cache to store the last write timestamp per ticker
    private final Map<String, Instant> lastWriteMap = new ConcurrentHashMap<>();

    private static final Duration THROTTLE_INTERVAL = Duration.ofSeconds(5);

    public CryptoMessageListener(CryptoPriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @RabbitListener(queues = "crypto.market.feeds")
    public void handleCryptoFeed(CryptoPrice priceMessage) {
        String ticker = priceMessage.getTicker();
        Instant now = Instant.now();

        // 3. Thread-safe read and atomic logic check
        Instant lastWrite = lastWriteMap.get(ticker);

        if (lastWrite == null || Duration.between(lastWrite, now).compareTo(THROTTLE_INTERVAL) >= 0) {

            // 4. Update the state map immediately to minimize race windows
            lastWriteMap.put(ticker, now);

            try {
                log.info("Throttling pass: Writing {} tick to database at (${})", ticker, priceMessage.getPrice());

                // 5. Blocking I/O boundary (Virtual thread unmounts here)
                priceRepository.save(priceMessage);

            } catch (Exception e) {
                log.error("Failed to persist tick for {}", ticker, e);
                lastWriteMap.remove(ticker);
            }
        }
    }
}