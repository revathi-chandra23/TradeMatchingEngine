package com.java.tradeEngine.validation;

import com.java.tradeEngine.exceptions.InvalidCountryException;
import com.java.tradeEngine.model.Orders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
// Validates whether the order is coming from an allowed country.
public class CountryValidator implements OrderValidator {

    private static final Set<String> ALLOWED_COUNTRIES = new HashSet<>(Arrays.asList(
            "US","UK","IN","SG","JP","DE","FR" //only orders from specified countries
    ));


    @Override
    public void validate(Orders order) throws Exception {
        if (!ALLOWED_COUNTRIES.contains(order.getCountryCode().toUpperCase())) {
            // if country not allowed it rejected the order
            throw new InvalidCountryException("Country not allowed: " + order.getCountryCode());
        }

    }
}
