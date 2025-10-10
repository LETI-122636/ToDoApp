package iscteiul.ista.pdf.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

@Route("pdf")
@PageTitle("PDF Generator")
@Menu(order = 0, icon = "vaadin:file-text", title = "PDF Generator")
public class PDFView extends VerticalLayout {

    private final PDFService pdfService;

    public PDFView(PDFService pdfService) {
        this.pdfService = pdfService;
        setPadding(true);
        setSpacing(true);

        TextField fileName = new TextField("File name");

        TextField title = new TextField("Title");

        TextArea content = new TextArea("Content");
        content.setWidthFull();

        // 🔹 Cria o link, mas começa escondido
        Anchor downloadLink = new Anchor();
        downloadLink.getElement().setAttribute("download", true);
        downloadLink.setText("Baixar PDF");
        downloadLink.setVisible(false);

        Button generate = new Button("Gerar PDF", event -> {
            try {
                byte[] pdfBytes = pdfService.criarPDF(
                        fileName.getValue(),
                        title.getValue(),
                        content.getValue()
                );

                // Cria um recurso em memória
                StreamResource resource = new StreamResource(
                        fileName.getValue(),
                        () -> new ByteArrayInputStream(pdfBytes)
                );

                resource.setContentType("application/pdf");

                // Atualiza o link com o novo PDF
                downloadLink.setHref(resource);
                downloadLink.setVisible(true);

                Notification.show("PDF gerado com sucesso. Clique em 'Baixar PDF' para transferir.",
                        4000, Notification.Position.BOTTOM_START);

            } catch (Exception ex) {
                Notification.show("Erro ao gerar PDF: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
        });

        add(fileName, title, content, generate, downloadLink);
    }
}
