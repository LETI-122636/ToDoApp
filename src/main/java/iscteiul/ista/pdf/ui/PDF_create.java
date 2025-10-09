package iscteiul.ista.pdf.ui;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PDF_create {
    /**
     * Cria um PDF simples com título, conteúdo e rodapé e retorna os bytes.
     *
     * @param fileName Nome do arquivo PDF (apenas usado como sugestão de nome no download)
     * @param titulo   Título do documento
     * @param conteudo Conteúdo principal do PDF
     * @return array de bytes com o conteúdo do PDF
     * @throws IOException em caso de erro de escrita do PDF
     */
    public static byte[] criarPDF(String fileName, String titulo, String conteudo) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Criar uma página
            PDPage page = new PDPage();
            document.addPage(page);

            // Adicionar conteúdo à página
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Cabeçalho
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(titulo);
                contentStream.endText();

                // Conteúdo principal (note: showText não quebra linhas automaticamente)
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText(conteudo);
                contentStream.endText();

                // Rodapé (número de página)
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contentStream.newLineAtOffset(50, 50);
                contentStream.showText("Página 1");
                contentStream.endText();
            }

            // Salvar PDF para o stream em memória
            document.save(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            // Repassa a exceção para o chamador lidar (UI/Service)
            throw e;
        }
    }
    /**
     * Escrita dos bytes PDF para o HttpServletResponse para download no navegador.
     *
     * @param fileName nome sugerido para download
     * @param pdfBytes conteúdo do PDF
     * @param response HttpServletResponse onde o PDF será escrito
     * @throws IOException em caso de erro de I/O
     */
    public static void transferToResponse(String fileName, byte[] pdfBytes, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        // Habilita compatibilidade com nomes não-ASCII
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        String disposition = "attachment; filename=\"" + fileName.replace("\"", "") + "\"; filename*=UTF-8''" + encoded;
        response.setHeader("Content-Disposition", disposition);
        response.setContentLength(pdfBytes.length);

        try (OutputStream out = response.getOutputStream()) {
            out.write(pdfBytes);
            out.flush();
        }
    }

    /**
     * Salva os bytes do PDF num ficheiro no disco.
     *
     * @param filePath caminho completo do ficheiro a criar/atualizar
     * @param pdfBytes conteúdo do PDF
     * @throws IOException em caso de erro de escrita
     */
    public static void saveToFile(String filePath, byte[] pdfBytes) throws IOException {
        Path path = Paths.get(filePath);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Files.write(path, pdfBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}