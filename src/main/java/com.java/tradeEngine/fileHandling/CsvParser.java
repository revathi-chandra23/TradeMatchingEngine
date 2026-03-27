package com.java.tradeEngine.fileHandling;

import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;
import com.java.tradeEngine.model.TradeType;
import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    //date and time formatter string  to localTimeDate
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<Orders> readAllOrders(String filePath) throws IOException {
        List<Orders> orders = new ArrayList<>();
        // reading csv files
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(","); //split by delimiter
                if (parts.length < 8) continue;

                Orders order = Orders.builder()   //build orders object from csv fields
                        .orderId(parts[0].trim())
                        .traderId(parts[1].trim())
                        .tradeType(TradeType.valueOf(parts[2].trim()))
                        .orderType(OrderType.valueOf(parts[3].trim()))
                        .price(Double.parseDouble(parts[4].trim()))
                        .quantity(Long.parseLong(parts[5].trim()))
                        .countryCode(parts[6].trim())
                        .timestamp(parseTimestamp(parts[7].trim()))
                        .build();

                orders.add(order); // add parsed orders to list
            }
        }
        return orders;
    }
//this timestamp is later used by:
//PriceTimeOrderBook
//FIFO priority matching convert string to time stamp
    public static Timestamp parseTimestamp(String timestamp) {
        LocalDateTime dateTime = LocalDateTime.parse(timestamp.trim(), formatter);
        return Timestamp.valueOf(dateTime);
    }
}
