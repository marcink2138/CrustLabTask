package currencyExchange;

import interfaces.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class CurrencyRatesReader implements CSVReader {
    @Override
    public String[] readCSV(String filePath) {
        Optional<String[]> currencyRates = Optional.empty();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i == 0) {
                    i++;
                    continue;
                }
                currencyRates = Optional.of(line.split(";"));
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currencyRates.orElseThrow(() -> new RuntimeException("Cannot read currency rates!"));
    }
}
