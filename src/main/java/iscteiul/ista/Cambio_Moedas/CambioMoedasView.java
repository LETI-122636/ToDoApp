package iscteiul.ista.Cambio_Moedas;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;
import java.util.Map;

/**
 * Interface gráfica (UI) para conversão de moedas.
 */
@Route("currency")
@PageTitle("Conversor de Moedas")
@Menu(order = 1, icon = "vaadin:money", title = "Conversor de Moedas")
public class CambioMoedasView extends VerticalLayout {

    private final CambioMoedasService currencyService;
    private ComboBox<String> fromCurrency;
    private ComboBox<String> toCurrency;
    private NumberField amount;
    private Div resultDiv;
    private Span resultText;

    public CambioMoedasView(CambioMoedasService currencyService) {
        this.currencyService = currencyService;

        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        // Título
        Span title = new Span("Conversor de Moedas");
        title.getStyle().set("font-size", "24px")
                .set("font-weight", "bold")
                .set("margin-bottom", "20px");

        // Campos de seleção de moeda com bandeiras
        fromCurrency = createCurrencyComboBox("Moeda Origem");
        toCurrency = createCurrencyComboBox("Moeda Destino");

        amount = new NumberField("Valor a Converter");
        amount.setMin(0);
        amount.setWidth("300px");

        // Botão de conversão
        Button convertButton = new Button("Converter", event -> convertCurrency());
        convertButton.getStyle().set("margin-top", "20px");

        // Área de resultado
        resultDiv = createResultDiv();

        add(title, fromCurrency, toCurrency, amount, convertButton, resultDiv);

        // Carregar moedas disponíveis
        loadAvailableCurrencies();

        // Inicialmente esconder a área de resultado
        resultDiv.setVisible(false);
    }

    private ComboBox<String> createCurrencyComboBox(String label) {
        ComboBox<String> comboBox = new ComboBox<>(label);
        comboBox.setWidth("300px");

        // Renderer personalizado para mostrar bandeira + código + nome
        comboBox.setRenderer(new ComponentRenderer<>(currencyCode -> {
            if (currencyCode == null) {
                return new Span("Selecione uma moeda");
            }

            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setAlignItems(Alignment.CENTER);

            try {
                // Bandeira
                String countryCode = currencyService.getCountryCode(currencyCode);
                Image flag = new Image(
                        "https://flagcdn.com/24x18/" + countryCode + ".png",
                        currencyCode
                );
                flag.setWidth("24px");
                flag.setHeight("18px");

                // Código e nome
                Span code = new Span(currencyCode);
                code.getStyle().set("font-weight", "bold")
                        .set("min-width", "50px");

                layout.add(flag, code);
            } catch (Exception e) {
                layout.add(new Span(currencyCode));
            }

            return layout;
        }));

        return comboBox;
    }

    private Div createResultDiv() {
        Div resultContainer = new Div();
        resultContainer.getStyle()
                .set("border", "2px solid #e8e8e8")
                .set("border-radius", "10px")
                .set("padding", "20px")
                .set("margin-top", "20px")
                .set("background-color", "#f8f9fa")
                .set("text-align", "center")
                .set("min-width", "400px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        // Título do resultado
        Span resultTitle = new Span("Resultado da Conversão");
        resultTitle.getStyle()
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("color", "#333")
                .set("display", "block")
                .set("margin-bottom", "15px");

        // Texto do resultado
        resultText = new Span();
        resultText.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "#2e7d32")
                .set("display", "block")
                .set("padding", "10px")
                .set("background-color", "white")
                .set("border-radius", "5px")
                .set("border", "1px solid #ddd");

        // Mensagem inicial
        resultText.setText("O resultado aparecerá aqui após a conversão");
        resultText.getStyle().set("color", "#666").set("font-size", "16px").set("font-weight", "normal");

        resultContainer.add(resultTitle, resultText);
        return resultContainer;
    }

    private void loadAvailableCurrencies() {
        try {
            Map<String, String> currencies = currencyService.getAvailableCurrencies();

            // Converter para array de códigos de moeda
            String[] currencyCodes = currencies.keySet().toArray(new String[0]);

            fromCurrency.setItems(currencyCodes);
            toCurrency.setItems(currencyCodes);

            // Valores padrão
            fromCurrency.setValue("EUR");
            toCurrency.setValue("USD");

        } catch (IOException e) {
            Notification.show("Erro ao carregar moedas disponíveis: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
        }
    }

    private void convertCurrency() {
        try {
            // Validações
            if (fromCurrency.getValue() == null || toCurrency.getValue() == null) {
                Notification.show("Por favor, selecione ambas as moedas",
                        3000, Notification.Position.MIDDLE);
                return;
            }

            if (amount.getValue() == null || amount.getValue() <= 0) {
                Notification.show("Por favor, insira um valor válido maior que zero",
                        3000, Notification.Position.MIDDLE);
                return;
            }

            // Verificar se as moedas são iguais
            if (fromCurrency.getValue().equals(toCurrency.getValue())) {
                Notification.show("Erro: Não é possível converter moeda igual para igual (" +
                                fromCurrency.getValue() + " para " + toCurrency.getValue() + ")",
                        5000, Notification.Position.MIDDLE);
                // Esconder resultado se houver erro
                resultDiv.setVisible(false);
                return;
            }

            double result = currencyService.converter(
                    fromCurrency.getValue(),
                    toCurrency.getValue(),
                    amount.getValue()
            );

            // Mostrar resultado na div
            showResult(result);

        } catch (Exception e) {
            Notification.show("Erro ao converter: " + e.getMessage(),
                    5000, Notification.Position.MIDDLE);
            // Esconder resultado se houver erro
            resultDiv.setVisible(false);
        }
    }

    private void showResult(double result) {
        // Formatar os valores
        String formattedAmount = String.format("%,.2f", amount.getValue());
        String formattedResult = String.format("%,.2f", result);

        // Obter códigos dos países para as bandeiras
        String fromCountryCode = currencyService.getCountryCode(fromCurrency.getValue());
        String toCountryCode = currencyService.getCountryCode(toCurrency.getValue());

        // Criar layout horizontal para o resultado com bandeiras
        HorizontalLayout resultLayout = new HorizontalLayout();
        resultLayout.setSpacing(true);
        resultLayout.setAlignItems(Alignment.CENTER);
        resultLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Parte esquerda: valor original
        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setSpacing(true);
        leftSide.setAlignItems(Alignment.CENTER);

        Image fromFlag = new Image("https://flagcdn.com/20x15/" + fromCountryCode + ".png", fromCurrency.getValue());
        fromFlag.setWidth("20px");
        fromFlag.setHeight("15px");

        Span fromValue = new Span(formattedAmount + " " + fromCurrency.getValue());
        fromValue.getStyle().set("font-size", "20px");

        leftSide.add(fromFlag, fromValue);

        // Seta de conversão
        Span arrow = new Span("→");
        arrow.getStyle().set("font-size", "24px").set("margin", "0 10px").set("color", "#666");

        // Parte direita: valor convertido
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setSpacing(true);
        rightSide.setAlignItems(Alignment.CENTER);

        Image toFlag = new Image("https://flagcdn.com/20x15/" + toCountryCode + ".png", toCurrency.getValue());
        toFlag.setWidth("20px");
        toFlag.setHeight("15px");

        Span toValue = new Span(formattedResult + " " + toCurrency.getValue());
        toValue.getStyle().set("font-size", "20px").set("font-weight", "bold").set("color", "#2e7d32");

        rightSide.add(toFlag, toValue);

        // Adicionar tudo ao layout principal
        resultLayout.add(leftSide, arrow, rightSide);

        // Limpar e adicionar o novo conteúdo
        resultText.removeAll();
        resultText.add(resultLayout);

        // Atualizar estilos do texto de resultado
        resultText.getStyle()
                .set("color", "#2e7d32")
                .set("font-size", "20px")
                .set("font-weight", "normal")
                .set("padding", "15px")
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("border", "2px solid #4caf50");

        // Mostrar a div de resultado
        resultDiv.setVisible(true);

        // Opcional: também mostrar uma notificação rápida
        Notification.show("Conversão realizada com sucesso!",
                3000, Notification.Position.BOTTOM_START);
    }
}