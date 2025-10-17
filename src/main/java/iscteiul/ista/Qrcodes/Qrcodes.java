package iscteiul.ista.Qrcodes;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Route("qrcodes")
@PageTitle("QR Codes")
@Menu(order = 1, icon = "vaadin:qrcode", title = "QR Codes")
public class Qrcodes extends Main {

    private final TextField inputField;
    private final Button generateBtn;
    private final Image qrImage;

    public Qrcodes() {
        inputField = new TextField("Texto para QR Code");
        inputField.setWidth("20em");

        generateBtn = new Button("Gerar QR Code", event -> gerarQrCode());
        generateBtn.addClassName(LumoUtility.Margin.Top.SMALL);

        qrImage = new Image();
        qrImage.setAlt("QR Code gerado");
        qrImage.setWidth("250px");
        qrImage.setHeight("250px");
        qrImage.setVisible(false);

        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(inputField, generateBtn, qrImage);
    }

    private void gerarQrCode() {
        String texto = inputField.getValue();
        if (texto == null || texto.isEmpty()) {
            Notification.show("Insira texto para gerar o QR Code.");
            return;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 250, 250);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            StreamResource resource = new StreamResource("qrcode.png", () -> new ByteArrayInputStream(outputStream.toByteArray()));
            qrImage.setSrc(resource);
            qrImage.setVisible(true);
        } catch (WriterException | IOException e) {
            Notification.show("Erro ao gerar QR Code: " + e.getMessage());
        }
    }
}
