 TRADE MATCHING ENGINE 
---------------------------------------------
###  **Overview**

This project implements a high-performance trade matching engine similar to those used in stock exchanges.

It reads BUY/SELL orders from CSV files, validates them, places them into an Order Book, and uses a Price-Time Priority Matching Strategy to execute trades.

The engine supports:
1. [ ] 
2. [ ] Loads orders from CSV files
3. [ ] 
4. [ ] Validates each order
5. [ ] 
6. [ ] Stores orders inside an Order Book (Price–Time priority)
7. [ ] 
8. [ ] Matches BUY vs SELL orders
9. [ ] 
10. [ ] Handles partial fills
11. [ ] 
12. [ ] Updates order status (PENDING, PARTIAL, FILLED, REJECTED)
13. [ ] 
14. [ ] Supports concurrency (multiple threads submitting orders at the same time)
-----------------------
### 📂 Project Structure 

src/main/java/org/atyeti/java/tradeEngine



#### ├── book/

│   ├── OrderBook.java

│   ├── PriceTimeOrderBook.java

│   └── OrderBookManager.java


#### ├── fileHandling/

│   └── CsvParser.java


#### ├── matchingEngine/

│   ├── MatchingEngine.java

│   ├── MatchingStrategy.java

│   └── PriceTimePriorityStrategy.java


#### ├── exceptions/
│   ├── AmountLimitExceededException

│   ├── InsufficientQuantityException

│   ├── InvalidCountryException

│   ├── InvalidOrderException

│   └── ValidationException


#### ├── model/

│   ├── Orders.java

│   ├── OrderType.java

│   ├── OrderStatus.java

│   ├── TradeType.java

│   └── Trade.java
│

#### ├── validation/

│   ├── OrderValidator.java

│   ├── FieldValidator.java

│   ├── CountryValidator.java

│   ├── MaxAmountValidator.java

│   └── OrderValidationService.java

│
#### └── Main.java

---------------------------------
### How to Run the Application
 
* Requirements

    * Java 17+

    * Maven or IntelliJ/Eclipse/VSCode

Run Steps

Place CSV files inside:

* src/main/resources/buy_orders.csv

* src/main/resources/sell_orders.csv


Run the main program:

 * src/main/java/org/atyeti/java/tradeEngine/Main.java

The engine will:
2. [ ] Validate orders
3. [ ] 
4. [ ] Process matches
5. [ ] 
6. [ ] Print summary in the console

-------------------------

### _packages explanation_ 

#### book/

Contains the Order Book implementation, responsible for storing BUY and SELL orders.

PriceTimeOrderBook → Uses two priority queues (BUY / SELL)

BUY: Highest price first, then earliest timestamp

SELL: Lowest price first, then earliest timestamp

OrderBookManager → Provides an order book per trade type (EQUITY, FOREX, CRYPTO)

#### matchingEngine/

Implements the logic to match orders.

MatchingEngine

* Calls strategy

* Updates order status (Filled / Partially Filled / Pending)

PriceTimePriorityStrategy -->

* Core matching logic

* Checks best opposite order

* Performs partial/full trades

* Removes filled orders from the book

MatchingStrategy (Interface)

* Supports pluggable matching algorithms


##### model/

Contains all domain classes:

Orders – A single order

OrderStatus – FILLED, PARTIALLY_FILLED, etc

OrderType – BUY or SELL

TradeType – EQUITY, FOREX, CRYPTO

Trade – (Not used currently but ready for storing trade records)


##### validation/

Implements the Chain of Responsibility pattern for validating orders.

Validators include:

* FieldValidator – Null checks, price, quantity, etc

* CountryValidator – Allowed country codes

* MaxAmountValidator – Limit based on trade type

* OrderValidationService – Runs all validators in sequence

##### fileHandling/

**CsvParser**

* Reads CSV

* Converts each row into an Orders object

##### exceptions/

* Custom exceptions thrown by validators.

----------------------------------------------
### Design Patterns Used

* Strategy Pattern

  * Used in MatchingEngine to allow different matching algorithms.
  * You can switch strategies without changing the engine
  
  
* Chain of responsibility pattern
  * Used for validation.
  * Each validator checks one condition and passes the order to the next.

  Benefits:

    * Easily add/remove validations 
    * Cleaner and more maintainable

* Singleton  OrderBookManager

   * Ensures one order book per market, no duplicates.

* Builder Pattern 
  *  used for creating orders cleanly using lombok.
------------------------------------------

### **Solid principles**


| SOLID Rule                    | How the code follows it                                              |
|-------------------------------|----------------------------------------------------------------------|
| **S - Single Responsibility** | Validators validate only. Matching engine matches only.              |
| **O - Open/Closed**           | Add new validator/matching strategy without modifying existing code. |
| **L - Liskov Substitution**   | Any MatchingStrategy can replace another.                            |
| **I - Interface Segregation** | Simple interfaces: OrderValidator, MatchingStrategy.                 |
| **D - Dependency Inversion**  | MatchingEngine depends on *strategy interface*, not implementation.  |


------------------------------------------------------------------------------------------------
### Concurrency Approach

The system ensures thread safety by:

*  Synchronized methods inside PriceTimeOrderBook

Prevents two threads from modifying the order book at the same time.

*  Concurrency Test (100 threads)

   * The test ensures:

   * No negative quantities

   * No duplicate modifications

   * Status is always valid

    * Engine handles 10,000 operations safely
-----------------------------------------------------
### Matching Rules (Simplified)

-->  BUY order matches SELL order if:
* buy.price >= bestSell.price

-->  SELL order matches BUY order if:
* sell.price <= bestBuy.price

Quantity Rules:

-- If order quantity is larger → Partial fill

-- If fully consumed → Removed from the book

Engine sets status:

FILLED, PARTIALLY_FILLED, PENDING, REJECTED

----------------------------------------------------------
### Conclusion

This project demonstrates:

* A fully working Price–Time Priority Matching Engine

* Strong architecture using design patterns

* Concurrency-safe order handling

* Highly testable and extendable structure