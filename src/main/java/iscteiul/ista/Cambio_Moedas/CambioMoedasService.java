package iscteiul.ista.Cambio_Moedas;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Serviço que encapsula a lógica de conversão de moedas.
 */
@Service
public class CambioMoedasService {

    /**
     * Converte um valor entre duas moedas.
     *
     * @param fromCurrency moeda origem (ex: "EUR")
     * @param toCurrency moeda destino (ex: "USD")
     * @param amount valor a converter
     * @return valor convertido
     * @throws IOException se houver falha ao consultar API
     */
    public double converter(String fromCurrency, String toCurrency, double amount) throws IOException {
        // Validação adicional para moedas iguais
        if (fromCurrency.equals(toCurrency)) {
            throw new IOException("Não é possível converter moeda igual para igual: " + fromCurrency);
        }
        return CambioMoedasUtils.converter(fromCurrency, toCurrency, amount);
    }

    /**
     * Obtém a lista de moedas disponíveis
     */
    public Map<String, String> getAvailableCurrencies() throws IOException {
        JSONObject currenciesJson = CambioMoedasUtils.getAvailableCurrencies();
        Map<String, String> currencies = new TreeMap<>();

        // Converter JSONObject para Map ordenado
        for (String key : currenciesJson.keySet()) {
            currencies.put(key, currenciesJson.getString(key));
        }

        return currencies;
    }

    /**
     * Obtém o código do país para uma moeda
     */
    public String getCountryCode(String currencyCode) {
        return CambioMoedasUtils.getCountryCode(currencyCode);
    }
}