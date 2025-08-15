package com.noel.currency_converter;

import com.noel.currency_converter.controller.CurrencyController;
import com.noel.currency_converter.exception.InvalidAmountException;
import com.noel.currency_converter.exception.InvalidBaseCurrencyException;
import com.noel.currency_converter.exception.InvalidDateException;
import com.noel.currency_converter.model.ConversionResponse;
import com.noel.currency_converter.model.RatesResponse;
import com.noel.currency_converter.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class CurrencyControllerTest {

    private CurrencyService currencyService;
    private CurrencyController controller;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        currencyService = Mockito.mock(CurrencyService.class);
        controller = new CurrencyController();
        // Use reflection to inject the mock
        Field field = CurrencyController.class.getDeclaredField("currencyService");
        field.setAccessible(true);
        field.set(controller, currencyService);
    }

    @Test
    void testGetLatestRates_validBase() {
        RatesResponse mockResponse = new RatesResponse();
        Mockito.when(currencyService.getLatestRates("USD")).thenReturn(mockResponse);
        assertEquals(mockResponse, controller.getLatestRates("USD"));
    }

    @Test
    void testGetLatestRates_invalidBase() {
        Exception ex = assertThrows(InvalidBaseCurrencyException.class, () -> controller.getLatestRates("XXX"));
        assertTrue(ex.getMessage().contains("Invalid currency"));
    }

    @Test
    void testGetHistoricalRates_valid() {
        RatesResponse mockResponse = new RatesResponse();
        Mockito.when(currencyService.getHistoricalRates(anyString(), anyString(), anyString())).thenReturn(mockResponse);
        assertEquals(mockResponse, controller.getHistoricalRates("USD", "2023-01-01", "EUR,GBP"));
    }

    @Test
    void testGetHistoricalRates_invalidDate() {
        Exception ex = assertThrows(InvalidDateException.class, () -> controller.getHistoricalRates("USD", "20230101", "EUR"));
        assertTrue(ex.getMessage().contains("Invalid date format"));
    }

    @Test
    void testGetHistoricalRates_invalidSymbol() {
        Exception ex = assertThrows(InvalidBaseCurrencyException.class, () -> controller.getHistoricalRates("USD", "2023-01-01", "XXX"));
        assertTrue(ex.getMessage().contains("Invalid currency"));
    }

    @Test
    void testConvertAmount_valid() {
        ConversionResponse mockResponse = new ConversionResponse();
        Mockito.when(currencyService.convertAmount(anyString(), anyString(), anyDouble())).thenReturn(mockResponse);
        assertEquals(mockResponse, controller.convertAmount("USD", "EUR", 10));
    }

    @Test
    void testConvertAmount_invalidAmount() {
        Exception ex = assertThrows(InvalidAmountException.class, () -> controller.convertAmount("USD", "EUR", 0));
        assertTrue(ex.getMessage().contains("at least 1"));
    }

    @Test
    void testConvertAmount_invalidCurrency() {
        Exception ex = assertThrows(InvalidBaseCurrencyException.class, () -> controller.convertAmount("XXX", "EUR", 10));
        assertTrue(ex.getMessage().contains("Invalid currency"));
    }
}