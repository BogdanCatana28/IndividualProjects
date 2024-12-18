package bnv.registru.registru.services.impl;

import bnv.registru.registru.services.OrderDocumentService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class OrderDocumentServiceImpl implements OrderDocumentService {

    public ByteArrayOutputStream generateOrderDocument(Long orderId, String registrationDate, String recipient) throws IOException {

        System.out.println("Registration Date: " + registrationDate);
        System.out.println("Recipient: " + recipient);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(registrationDate);
        String formattedDate = date.format(formatter);

        InputStream templateStream = getClass().getClassLoader().getResourceAsStream("templates/order_template.docx");

        if (templateStream == null) {
            throw new FileNotFoundException("Template file not found.");
        }

        XWPFDocument document = new XWPFDocument(templateStream);

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    text = text.replace("{orderId}", String.valueOf(orderId))
                            .replace("{registrationDate}", formattedDate)
                            .replace("{recipient}", recipient);
                    run.setText(text, 0);
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        return outputStream;
    }
}

