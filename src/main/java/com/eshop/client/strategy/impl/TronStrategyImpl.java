package com.eshop.client.strategy.impl;

import com.eshop.client.strategy.NetworkStrategy;
import com.eshop.client.dto.TransactionResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
@Slf4j
public class TronStrategyImpl implements NetworkStrategy {
    private final RestTemplate restTemplate;
    private static final String baseUrl = "https://apilist.tronscanapi.com";

    @SneakyThrows
    @Override
    public BigDecimal getPrice(String token) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl.concat("/api/token/price"))
                .queryParam("token", token)
                .toUriString();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            String priceInUsd = rootNode.get("price_in_usd").asText();
            return new BigDecimal(priceInUsd);
        } else {
            throw new RuntimeException(String.format("Failed to fetch %s price, Status: %s", token,response.getStatusCode()));
        }
    }

    @SneakyThrows
    @Override
    public TransactionResponse getTransactionInfo(String hash){
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl.concat("/api/transaction-info"))
                .queryParam("hash", hash)
                .toUriString();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return mapFromJson(response.getBody());
        } else {
            throw new RuntimeException("Failed to fetch transaction info, Status: " + response.getStatusCode());
        }
    }

    public static TransactionResponse mapFromJson(String json) throws IOException {
        if(json.trim() == null || json.trim().isEmpty() || json.trim().equals("{}"))
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);

        TransactionResponse response = new TransactionResponse();

        // Map contractRet
        if (rootNode.has("contractRet")) {
            response.setSuccess(rootNode.get("contractRet").asText().equals("SUCCESS"));
        }

        // Map ownerAddress
        if (rootNode.has("ownerAddress")) {
            response.setFromAddress(rootNode.get("ownerAddress").asText());
        }

        // Map from tokenTransferInfo or trc20TransferInfo
        JsonNode transferNode = rootNode.path("tokenTransferInfo");
        if (!transferNode.isMissingNode()) {
            if(transferNode.has("amount_str")) {
                var amount = new BigDecimal(transferNode.get("amount_str").asText());
                if(amount == null)
                    response.setAmount(BigDecimal.ZERO);
                else response.setAmount(amount.divide(BigDecimal.valueOf(1000000)).setScale(4, RoundingMode.HALF_UP));
            }
            if(transferNode.has("to_address"))
                response.setToAddress(transferNode.get("to_address").asText());
        } else {
            transferNode = rootNode.path("trc20TransferInfo").path(0);
            if (!transferNode.isMissingNode()) {
                if(transferNode.has("amount_str")) {
                    var amount = new BigDecimal(transferNode.get("amount_str").asText());
                    if(amount == null)
                        response.setAmount(BigDecimal.ZERO);
                    else response.setAmount(amount.divide(BigDecimal.valueOf(1000000)).setScale(4, RoundingMode.HALF_UP));
                }
                if(transferNode.has("to_address"))
                    response.setToAddress(transferNode.get("to_address").asText());
            }
        }
        return response;
    }
}
