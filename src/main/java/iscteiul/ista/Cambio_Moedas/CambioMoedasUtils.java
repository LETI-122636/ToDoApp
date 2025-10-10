package iscteiul.ista.Cambio_Moedas;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * Utilitário para buscar taxas de câmbio em tempo real
 * usando a API pública Frankfurter (sem necessidade de chave de acesso).
 */
public class CambioMoedasUtils {

    private static final String API_URL = "https://api.frankfurter.app/latest";
    private static final String CURRENCIES_URL = "https://api.frankfurter.app/currencies";

    public static double converter(String fromCurrency, String toCurrency, double amount) throws IOException {
        // Verificar se as moedas são iguais
        if (fromCurrency.equals(toCurrency)) {
            throw new IOException("Não é possível converter moeda igual para igual: " + fromCurrency);
        }

        String urlStr = API_URL + "?from=" + fromCurrency + "&to=" + toCurrency;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (Scanner sc = new Scanner(conn.getInputStream())) {
            StringBuilder response = new StringBuilder();
            while (sc.hasNext()) {
                response.append(sc.nextLine());
            }

            JSONObject json = new JSONObject(response.toString());

            if (!json.has("rates") || !json.getJSONObject("rates").has(toCurrency)) {
                throw new IOException("Moeda destino inválida ou não suportada: " + toCurrency);
            }

            double taxa = json.getJSONObject("rates").getDouble(toCurrency);
            return amount * taxa;
        } catch (IOException e) {
            throw new IOException("Erro ao contactar a API: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém a lista de moedas disponíveis da API
     */
    public static JSONObject getAvailableCurrencies() throws IOException {
        URL url = new URL(CURRENCIES_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (Scanner sc = new Scanner(conn.getInputStream())) {
            StringBuilder response = new StringBuilder();
            while (sc.hasNext()) {
                response.append(sc.nextLine());
            }
            return new JSONObject(response.toString());
        } catch (IOException e) {
            throw new IOException("Erro ao obter lista de moedas: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém o código do país para uma moeda (para a bandeira)
     */
    public static String getCountryCode(String currencyCode) {
        // Mapeamento de códigos de moeda para códigos de país
        java.util.Map<String, String> currencyToCountry = java.util.Map.ofEntries(
                java.util.Map.entry("EUR", "eu"), // União Europeia
                java.util.Map.entry("USD", "us"),
                java.util.Map.entry("GBP", "gb"),
                java.util.Map.entry("JPY", "jp"),
                java.util.Map.entry("CAD", "ca"),
                java.util.Map.entry("AUD", "au"),
                java.util.Map.entry("CHF", "ch"),
                java.util.Map.entry("CNY", "cn"),
                java.util.Map.entry("SEK", "se"),
                java.util.Map.entry("NZD", "nz"),
                java.util.Map.entry("MXN", "mx"),
                java.util.Map.entry("SGD", "sg"),
                java.util.Map.entry("HKD", "hk"),
                java.util.Map.entry("NOK", "no"),
                java.util.Map.entry("KRW", "kr"),
                java.util.Map.entry("TRY", "tr"),
                java.util.Map.entry("RUB", "ru"),
                java.util.Map.entry("INR", "in"),
                java.util.Map.entry("BRL", "br"),
                java.util.Map.entry("ZAR", "za"),
                java.util.Map.entry("DKK", "dk"),
                java.util.Map.entry("PLN", "pl"),
                java.util.Map.entry("THB", "th"),
                java.util.Map.entry("IDR", "id"),
                java.util.Map.entry("HUF", "hu"),
                java.util.Map.entry("CZK", "cz"),
                java.util.Map.entry("ILS", "il"),
                java.util.Map.entry("CLP", "cl"),
                java.util.Map.entry("PHP", "ph"),
                java.util.Map.entry("AED", "ae"),
                java.util.Map.entry("COP", "co"),
                java.util.Map.entry("SAR", "sa"),
                java.util.Map.entry("MYR", "my"),
                java.util.Map.entry("RON", "ro")
        );

        return currencyToCountry.getOrDefault(currencyCode, "us"); // Default para US
    }
}