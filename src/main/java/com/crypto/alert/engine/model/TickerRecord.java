package com.crypto.alert.engine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerRecord(
        @JsonProperty("product_id")
        String productId,
        @JsonProperty("price")
        String price
) {}