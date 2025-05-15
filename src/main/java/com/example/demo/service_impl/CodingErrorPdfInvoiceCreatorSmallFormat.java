package com.example.demo.service_impl;//package com.example.demo.service_impl;

import com.example.demo.model.bill.AddressDetails;
import com.example.demo.model.bill.HeaderDetails;
import com.example.demo.model.bill.Product;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.FileNotFoundException;
import java.util.List;
public class CodingErrorPdfInvoiceCreatorSmallFormat {
    private static final float INVOICE_WIDTH = 210f;
    private static final float INVOICE_HEIGHT = 297f; // Full A4 height (we'll use less space)

    Document document;
    PdfDocument pdfDocument;
    String pdfName;

    // Adjusted column widths for compact layout
    float threecol = 60f;
    float twocol = 90f;
    float twocol150 = twocol + 45f;
    float twocolumnWidth[] = {twocol150, twocol};
    float threeColumnWidth[] = {threecol, threecol, threecol};
    float fullwidth[] = {threecol * 3};

    public CodingErrorPdfInvoiceCreatorSmallFormat(String pdfName) {
        this.pdfName = pdfName;
    }

    public void createDocument() throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(pdfName);
        pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(new PageSize(INVOICE_WIDTH, INVOICE_HEIGHT));
        this.document = new Document(pdfDocument);
        document.setMargins(10, 10, 10, 10); // Reduced margins
    }

    public void createSinglePageInvoice(HeaderDetails header,
                                        AddressDetails address,
                                        List<Product> products,
                                        List<String> terms) {
        try {
            createDocument();

            // 1. Header (more compact)
            createCompactHeader(header);

            // 2. Address (single column)
            createCompactAddress(address);

            // 3. Products (more compact)
            createCompactProducts(products);

            // 4. Terms (very compact)
            createCompactTerms(terms);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createCompactHeader(HeaderDetails header) {
        Table table = new Table(new float[]{140f, 70f});
        table.setFontSize(8f);

        table.addCell(new Cell().add(header.getInvoiceTitle())
                .setFontSize(12f)
                .setBold()
                .setBorder(Border.NO_BORDER));

        Table details = new Table(new float[]{35f, 35f});
        details.addCell(createCell("Nr. i re:", true).setTextAlignment(TextAlignment.RIGHT));
        details.addCell(createCell(header.getInvoiceNo(), false));
        details.addCell(createCell("Data:", true).setTextAlignment(TextAlignment.RIGHT));
        details.addCell(createCell(header.getInvoiceDate(), false));

        table.addCell(new Cell().add(details).setBorder(Border.NO_BORDER));
        document.add(table);
        document.add(new Paragraph("").setMarginBottom(5f));
    }

    private void createCompactAddress(AddressDetails address) {
        // Single column address to save space
        Paragraph addressPara = new Paragraph()
                .add(address.getBillingCompany() + "\n")
                .add(address.getBillingName() + "\n")
                .add(address.getBillingAddress() + "\n")
                .add(address.getBillingEmail())
                .setFontSize(7f)
                .setMarginBottom(5f);
        document.add(addressPara);
    }

    private void createCompactProducts(List<Product> products) {
        Table table = new Table(threeColumnWidth);
        table.setFontSize(7f);

        // Header
        table.addCell(createCell("Produkti", true));
        table.addCell(createCell("Sasia", true).setTextAlignment(TextAlignment.CENTER));
        table.addCell(createCell("Çmimi", true).setTextAlignment(TextAlignment.RIGHT));

        // Products
        float total = 0;
        for (Product p : products) {
            float itemTotal = p.getQuantity() * p.getPriceperpeice();
            total += itemTotal;

            table.addCell(createCell(p.getPname().orElse(""), false));
            table.addCell(createCell(p.getQuantity() + "x" + p.getPriceperpeice(), false)
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(createCell(String.format("%.2f", itemTotal), false)
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        // Total
        table.addCell(createCell("", false));
        table.addCell(createCell("TOTALI", true).setTextAlignment(TextAlignment.CENTER));
        table.addCell(createCell(String.format("%.2f", total), true)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(table);
        document.add(new Paragraph("").setMarginBottom(5f));
    }

    private void createCompactTerms(List<String> terms) {
        if (terms == null || terms.isEmpty()) return;

        Paragraph termsHeader = new Paragraph("KUSHTET:")
                .setBold()
                .setFontSize(7f)
                .setMarginBottom(3f);
        document.add(termsHeader);

        for (String term : terms) {
            document.add(new Paragraph("- " + term).setFontSize(6f).setMarginBottom(1f));
        }
    }

    private Cell createCell(String content, boolean bold) {
        Cell cell = new Cell().add(content).setBorder(Border.NO_BORDER).setFontSize(7f);
        return bold ? cell.setBold() : cell;
    }

    // ... keep all your existing utility methods ...
}