package qrcodes.ui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Qrcodes {

    // Método utilitário para gerar QR Code
    public static void gerarQrCode(String text, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        System.out.println("QR Code gerado em: " + path.toAbsolutePath());
    }

    // Método main apenas para teste manual (pode remover se quiser)
    public static void main(String[] args) {
        try {
            gerarQrCode("https://www.example.com", "qrcode.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
