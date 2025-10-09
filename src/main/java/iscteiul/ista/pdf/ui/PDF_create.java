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

            PDType1Font fonteTitulo = PDType1Font.HELVETICA_BOLD;
            PDType1Font fonteTexto = PDType1Font.HELVETICA;
            int tamanhoFonteTitulo = 18;
            int tamanhoFonteTexto = 12;

            float margem = 50;
            float larguraMax = 500; // largura útil do texto
            float yStart = 750;
            float y = yStart;
            float espacoLinha = 15;

            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Cabeçalho
            contentStream.beginText();
            contentStream.setFont(fonteTitulo, tamanhoFonteTitulo);
            contentStream.newLineAtOffset(margem, y);
            contentStream.showText(titulo);
            contentStream.endText();

            y -= 40; // espaço após título

            contentStream.beginText();
            contentStream.setFont(fonteTexto, tamanhoFonteTexto);
            contentStream.newLineAtOffset(margem, y);

            String[] paragrafos = conteudo.split("\n");

            for (String paragrafo : paragrafos) {
                String[] linhas = quebrarLinha(paragrafo, fonteTexto, tamanhoFonteTexto, larguraMax);

                for (String linha : linhas) {
                    if (y <= margem + 40) { // fim da página
                        contentStream.endText();
                        contentStream.close();

                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        y = yStart;

                        contentStream.beginText();
                        contentStream.setFont(fonteTexto, tamanhoFonteTexto);
                        contentStream.newLineAtOffset(margem, y);
                    }

                    contentStream.showText(linha);
                    contentStream.newLineAtOffset(0, -espacoLinha);
                    y -= espacoLinha;
                }

                // Espaço entre parágrafos
                contentStream.newLineAtOffset(0, -espacoLinha);
                y -= espacoLinha;
            }

            contentStream.endText();

            // Rodapé
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            contentStream.newLineAtOffset(50, 50);
            contentStream.showText("Página 1");
            contentStream.endText();
            contentStream.close();

            document.save(baos);
            return baos.toByteArray();
        }
    }

    // Função auxiliar para quebrar linhas mesmo sem espaços
    private static String[] quebrarLinha(String texto, PDType1Font fonte, int tamanhoFonte, float larguraMax) throws IOException {
        java.util.List<String> linhas = new java.util.ArrayList<>();
        int inicio = 0;

        while (inicio < texto.length()) {
            int fim = inicio + 1;
            while (fim <= texto.length()) {
                String parte = texto.substring(inicio, fim);
                float largura = fonte.getStringWidth(parte) / 1000 * tamanhoFonte;
                if (largura > larguraMax) break;
                fim++;
            }
            linhas.add(texto.substring(inicio, fim - 1));
            inicio = fim - 1;
        }

        return linhas.toArray(new String[0]);
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