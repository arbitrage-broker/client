package com.eshop.client.strategy.impl;

import com.eshop.client.dto.*;
import com.eshop.client.strategy.NetworkStrategy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
@Slf4j
public class BnbStrategyImpl implements NetworkStrategy {
    private final RestTemplate restTemplate;
    private static final String apiKey = "PAR5QIAHG7JW2RIMMWCZK4Q2M8BYJJQBFS";
    private static final String baseUrl = "https://api.bscscan.com/api";

    @SneakyThrows
    @Override
    public BigDecimal getPrice(String token) {
        String url = UriComponentsBuilder.fromHttpUrl("https://api.binance.com/api/v3/ticker/price")
                .queryParam("symbol", token)//BNBUSDT
                .toUriString();
        ResponseEntity<BsPrice> response = restTemplate.getForEntity(url, BsPrice.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return new BigDecimal(response.getBody().getPrice());
        } else {
            throw new RuntimeException(String.format("Failed to fetch %s price, Status: %s", token,response.getStatusCode()));
        }
    }

    @SneakyThrows
    @Override
    public TransactionResponse getTransactionInfo(String hash) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("module", "proxy")
                .queryParam("action", "eth_getTransactionByHash")
                .queryParam("txhash", hash)
                .queryParam("apikey", apiKey)
                .toUriString();
        ResponseEntity<BsTxResponse> response = restTemplate.getForEntity(url, BsTxResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return mapFromJson(response.getBody());
        } else {
            throw new RuntimeException("Failed to fetch transaction info, Status: " + response.getStatusCode());
        }
    }

    private TransactionResponse mapFromJson(BsTxResponse bsTxResponse) throws IOException {
        if (bsTxResponse == null || bsTxResponse.getResult() == null)
            return null;
        TransactionResponse response = new TransactionResponse();
        // Map from result
        TransactionResult result = bsTxResponse.getResult();
        BigInteger valueWei = new BigInteger(result.getValue().replace("0x", ""), 16);
        BigDecimal amount = new BigDecimal(valueWei).divide(new BigDecimal("1000000000000000000")); // Convert Wei to BNB
        response.setAmount(amount.setScale(4, RoundingMode.HALF_UP));
        response.setToAddress(result.getTo());
        response.setFromAddress(result.getFrom());
        response.setSymbol(getTokenSymbol(result.getTo()));
        response.setSuccess(getTransactionReceiptStatus(bsTxResponse.getResult().getHash()));

        return response;
    }

    private String getTokenSymbol(String tokenAddress) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("module", "token")
                .queryParam("action", "tokeninfo")
                .queryParam("contractaddress", tokenAddress)
                .queryParam("apikey", apiKey)
                .toUriString();
        try {
            TokenInfoResponse response = new RestTemplate().getForObject(url, TokenInfoResponse.class);

            if (response != null && response.getResult() != null && !response.getResult().isEmpty()) {
                return response.getResult().get(0).getSymbol(); // Assuming the token symbol is in the first result
            }
            return "UNKNOWN TOKEN"; // Default return if symbol cannot be determined
        } catch (Exception e) {
            return "UNKNOWN TOKEN";
        }
    }

    private boolean getTransactionReceiptStatus(String txHash) {
        String receiptUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("module", "transaction")
                .queryParam("action", "gettxreceiptstatus")
                .queryParam("txhash", txHash)
                .queryParam("apikey", apiKey)
                .toUriString();
        try {
            TransactionReceiptResponse receiptResponse = new RestTemplate().getForObject(receiptUrl, TransactionReceiptResponse.class);

            if (receiptResponse != null && receiptResponse.getResult() != null) {
                return receiptResponse.getResult().getStatus().equals("1"); // Status field in the receipt, expected to be "0" or "1"
            }
            return false; // Default to failure if unable to retrieve the status
        } catch (Exception e) {
            return false;
        }
    }
}
