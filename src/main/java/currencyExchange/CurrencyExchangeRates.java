package currencyExchange;

import enums.Currency;
import interfaces.CSVReader;

public class CurrencyExchangeRates {
    private static final double PLN_USD;
    private static final double PLN_EUR;
    private static final double EUR_PLN;
    private static final double EUR_USD;
    private static final double USD_EUR;
    private static final double USD_PLN;

    static {
        CSVReader currencyRatesReader = new CurrencyRatesReader();
        String[] rates = currencyRatesReader.readCSV("src/main/resources/CurrencyExchangeRates.csv");
        PLN_USD = Double.parseDouble(rates[0]);
        PLN_EUR = Double.parseDouble(rates[1]);
        EUR_PLN = Double.parseDouble(rates[2]);
        EUR_USD = Double.parseDouble(rates[3]);
        USD_EUR = Double.parseDouble(rates[4]);
        USD_PLN = Double.parseDouble(rates[5]);
    }

    private CurrencyExchangeRates() {
    }

    public static double getProperExchangeRate(Currency sourceCurrency, Currency destinationCurrency) {
        if (sourceCurrency == Currency.PLN && destinationCurrency == Currency.EUR)
            return PLN_EUR;
        if (sourceCurrency == Currency.PLN && destinationCurrency == Currency.USD)
            return PLN_USD;
        if (sourceCurrency == Currency.EUR && destinationCurrency == Currency.PLN)
            return EUR_PLN;
        if (sourceCurrency == Currency.EUR && destinationCurrency == Currency.USD)
            return EUR_USD;
        if (sourceCurrency == Currency.USD && destinationCurrency == Currency.EUR)
            return USD_EUR;
        if (sourceCurrency == Currency.USD && destinationCurrency == Currency.PLN)
            return USD_PLN;
        return 1;
    }

}
