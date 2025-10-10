// java
package iscteiul.ista.pdf.ui;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class PDFService {

    /**
     * Delegates to the existing PDF_create utility to create a PDF and returns the bytes.
     *
     * @param fileName output file name (e.g. "Relatorio.pdf")
     * @param titulo   document title
     * @param conteudo main content
     * @return PDF as byte array
     * @throws IOException if PDF creation fails
     */
    public byte[] criarPDF(String fileName, String titulo, String conteudo) throws IOException {
        return PDF_create.criarPDF(fileName, titulo, conteudo);
    }

    /**
     * Generates the PDF and streams it to the given HttpServletResponse for download.
     *
     * @param fileName name suggested for download
     * @param titulo   document title
     * @param conteudo main content
     * @param response HttpServletResponse to write the PDF into
     * @throws IOException on I/O errors
     */
    public void transferToResponse(String fileName, String titulo, String conteudo, HttpServletResponse response) throws IOException {
        byte[] pdfBytes = criarPDF(fileName, titulo, conteudo);
        PDF_create.transferToResponse(fileName, pdfBytes, response);
    }
}