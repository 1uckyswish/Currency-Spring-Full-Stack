package com.noel.currency_converter.controller;

import com.noel.currency_converter.exception.InvalidAmountException;
import com.noel.currency_converter.exception.InvalidBaseCurrencyException;
import com.noel.currency_converter.exception.InvalidDateException;
import com.noel.currency_converter.model.ConversionResponse;
import com.noel.currency_converter.model.RatesResponse;
import com.noel.currency_converter.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    // ----------------- SUPPORTED CURRENCIES -----------------
    private final Set<String> supportedCurrencies = Set.of(
            "SDD","SDG","SEK","SGD","SHP","SIT","SKK","SLE","SLL","SOL","SOS","SPL","SRD","SRG",
            "STD","STN","SVC","SYP","SZL","THB","TJS","TMM","TMT","TND","TOP","TRL","TRY","TTD",
            "TVD","TWD","TZS","UAH","UGX","UNI","USD","USDC","USDP","UYU","UZS","VAL","VEB","VED",
            "VEF","VES","VND","VUV","WST","XAF","XAG","XAU","XBT","XCD","XCG","XDR","XLM","XOF",
            "XPD","XPF","XPT","XRP","YER","ZAR","ZMK","ZMW","ZWD","ZWG","ZWL","ADA","AED","AFN",
            "ALL","AMD","ANG","AOA","ARS","ATS","AUD","AWG","AZM","AZN","BAM","BBD","BCH","BDT",
            "BEF","BGN","BHD","BIF","BMD","BND","BOB","BRL","BSD","BTC","BTN","BWP","BYN","BYR",
            "BZD","CAD","CDF","CHF","CLF","CLP","CNH","CNY","COP","CRC","CUC","CUP","CVE","CYP",
            "CZK","DEM","DJF","DKK","DOGE","DOP","DOT","DZD","EEK","EGP","ERN","ESP","ETB","ETH",
            "EUR","EURC","FIM","FJD","FKP","FRF","GBP","GEL","GGP","GHC","GHS","GIP","GMD","GNF",
            "GRD","GTQ","GYD","HKD","HNL","HRK","HTG","HUF","IDR","IEP","ILS","IMP","INR","IQD",
            "IRR","ISK","ITL","JEP","JMD","JOD","JPY","KES","KGS","KHR","KMF","KPW","KRW","KWD",
            "KYD","KZT","LAK","LBP","LINK","LKR","LRD","LSL","LTC","LTL","LUF","LUNA","LVL","LYD",
            "MAD","MDL","MGA","MGF","MKD","MMK","MNT","MOP","MRO","MRU","MTL","MUR","MVR","MWK",
            "MXN","MXV","MYR","MZM","MZN","NAD","NGN","NIO","NLG","NOK","NPR","NZD","OMR","PAB",
            "PEN","PGK","PHP","PKR","PLN","PTE","PYG","QAR","ROL","RON","RSD","RUB","RWF","SAR",
            "SBD","SCR"
    );

    // ----------------- LATEST RATES -----------------
    @GetMapping("/latest")
    public RatesResponse getLatestRates(@RequestParam String base) {
        validateCurrency(base);
        return currencyService.getLatestRates(base);
    }

    // ----------------- HISTORICAL RATES -----------------
    @GetMapping("/historical")
    public RatesResponse getHistoricalRates(
            @RequestParam String base,
            @RequestParam String date,
            @RequestParam String symbols // now required
    ) {
        validateCurrency(base);
        validateDate(date);
        for (String symbol : symbols.split(",")) {
            validateCurrency(symbol.trim());
        }
        return currencyService.getHistoricalRates(base, date, symbols);
    }

    // ----------------- AMOUNT CONVERSION -----------------
    @GetMapping("/convert")
    public ConversionResponse convertAmount(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount
    ) {
        validateCurrency(from);
        validateCurrency(to);
        if (amount < 1) {
            throw new InvalidAmountException("Amount must be at least 1.");
        }
        return currencyService.convertAmount(from, to, amount);
    }

    // ----------------- VALIDATION METHODS -----------------
    private void validateCurrency(String currency) {
        if (!supportedCurrencies.contains(currency.toUpperCase())) {
            throw new InvalidBaseCurrencyException("Invalid currency: " + currency);
        }
    }

    private void validateDate(String date) {
        try {
            LocalDate.parse(date); // expects YYYY-MM-DD
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Invalid date format. Use YYYY-MM-DD: " + date);
        }
    }
}
