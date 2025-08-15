package com.noel.currency_converter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noel.currency_converter.exception.ApiCallException;
import com.noel.currency_converter.exception.InvalidAmountException;
import com.noel.currency_converter.exception.InvalidBaseCurrencyException;
import com.noel.currency_converter.exception.InvalidDateException;
import com.noel.currency_converter.model.ConversionResponse;
import com.noel.currency_converter.model.RatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Value("${currency.api.key}")
    private String apiKey;

    @Value("${currency.api.latest.url}")
    private String latestUrl;

    @Value("${currency.api.historical.url}")
    private String historicalUrl;

    @Value("${currency.api.convert.url}")
    private String convertUrl;

    // ----------------- GENERIC API FETCH -----------------
    private <T> T fetchFromApi(String urlLink, Class<T> clazz) {
        try {
            logger.info("Fetching data from API: {}", urlLink);

            URL url = new URL(urlLink);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            ObjectMapper mapper = new ObjectMapper();
            T response = mapper.readValue(request.getInputStream(), clazz);

            logger.info("API call successful");
            return response;

        } catch (Exception e) {
            logger.error("API call failed", e);
            throw new ApiCallException("Failed to fetch data from API");
        }
    }

    // ----------------- LATEST RATES -----------------
    public RatesResponse getLatestRates(String base) {
        validateCurrency(base);
        String url = String.format("%s?base=%s&api_key=%s", latestUrl, base, apiKey);
        return fetchFromApi(url, RatesResponse.class);
    }

    // ----------------- HISTORICAL RATES -----------------
    public RatesResponse getHistoricalRates(String base, String date, String symbols) {
        validateCurrency(base);
        validateDate(date);
        String url = String.format("%s?base=%s&date=%s&symbols=%s&api_key=%s",
                historicalUrl, base, date, symbols, apiKey);
        return fetchFromApi(url, RatesResponse.class);
    }

    // ----------------- AMOUNT CONVERSION -----------------
    public ConversionResponse convertAmount(String from, String to, double amount) {
        validateCurrency(from);
        validateCurrency(to);
        validateAmount(amount);
        String url = String.format("%s?from=%s&to=%s&amount=%s&api_key=%s",
                convertUrl, from, to, amount, apiKey);
        return fetchFromApi(url, ConversionResponse.class);
    }

    // ----------------- VALIDATORS -----------------
    private void validateCurrency(String currency) {
        if (currency == null || currency.length() != 3) {
            throw new InvalidBaseCurrencyException("Currency must be a 3-letter code");
        }
    }

    private void validateDate(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateException("Date must be in YYYY-MM-DD format");
        }
    }

    private void validateAmount(double amount) {
        if (amount < 1) {
            throw new InvalidAmountException("Amount must be greater than 0");
        }
    }
}
