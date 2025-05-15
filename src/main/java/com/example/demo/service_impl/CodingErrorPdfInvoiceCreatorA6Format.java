package com.example.demo.service_impl;

import com.example.demo.model.bill.*;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodingErrorPdfInvoiceCreatorA6Format {
    private Document document;
    private PdfDocument pdfDocument;
    private String pdfName;

    // A6 dimensions (105 x 148 mm)
    private static final PageSize A6 = new PageSize(210, 297); // 1/4 of A4 (equivalent to A6 when rotated)

    // Column widths for A6 format
    private final float[] singleColumnWidth = {170f};
    private final float[] twoColumnWidth = {100f, 70f};
    private final float[] threeColumnWidth = {60f, 60f, 50f};

    public CodingErrorPdfInvoiceCreatorA6Format(String pdfName) {
        this.pdfName = pdfName;
    }

    public void createSinglePageInvoice(HeaderDetails header,
                                        AddressDetails address,
                                        List<Product> products,
                                        List<String> terms) throws FileNotFoundException {
        createDocument();
        createCompactHeader(header);
        createCompactAddress(address);
        createCompactProducts(products);
        createCompactTerms(terms);
        document.close();
    }

    private void createDocument() throws FileNotFoundException {
        PdfWriter pdfWriter = new PdfWriter(pdfName);
        pdfDocument = new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(A6);
        this.document = new Document(pdfDocument);
        document.setMargins(10, 10, 10, 10); // Tight margins for small paper
    }

    private void createCompactHeader(HeaderDetails header) {
        // Main title remains bold and centered
        document.add(new Paragraph(header.getInvoiceTitle())
                .setBold()
                .setFontSize(12)  // Slightly smaller than before
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4f));

        // Invoice info - compact single column
        Table infoTable = new Table(new float[]{170f});
        infoTable.setFontSize(8);  // Set base font size for all cells

        // Add rows with bold labels
        addHeaderRow(infoTable, header.getInvoiceNoText(), header.getInvoiceNo());
        addHeaderRow(infoTable, header.getInvoiceDateText(), header.getInvoiceDate());
        addHeaderRow(infoTable, header.getInvoiceTimeText(),
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        document.add(infoTable);
        document.add(createDivider());
    }

    private void addHeaderRow(Table table, String label, String value) {
        Paragraph p = new Paragraph()
                .add(new Text(label + ": ").setBold())  // Only the label is bold
                .add(value);                            // Value remains normal
        table.addCell(new Cell()
                .add(p)
                .setBorder(Border.NO_BORDER)
                .setPadding(0)
                .setMargin(0));
    }

    private void createCompactAddress(AddressDetails address) {
        // Use two columns for location and contact info
        float[] addressColumns = {90f, 80f}; // Slightly narrower first column for labels

        Table addressTable = new Table(addressColumns);

        // Location Info (Left Column)
        addressTable.addCell(createCompactCell("Biznesi:", true, TextAlignment.LEFT));
        addressTable.addCell(createCompactCell("Kontakti:", true, TextAlignment.LEFT));

        addressTable.addCell(createCompactCell(address.getBillingCompany(), false, TextAlignment.LEFT));
        addressTable.addCell(createCompactCell(address.getBillingEmail(), false, TextAlignment.LEFT));

        addressTable.addCell(createCompactCell("Adresa:", true, TextAlignment.LEFT));
        addressTable.addCell(createCompactCell("Nr. Tel:", true, TextAlignment.LEFT));

        addressTable.addCell(createCompactCell(address.getBillingAddress(), false, TextAlignment.LEFT));
        addressTable.addCell(createCompactCell("+355666777", false, TextAlignment.LEFT));

        document.add(addressTable);
        document.add(createDivider());
    }

    private void createCompactProducts(List<Product> products) {
        // Group products by name to combine quantities
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(
                        p -> p.getPname().orElse(""),
                        p -> p,
                        (p1, p2) -> new Product(p1.getPname().orElse(""),
                                p1.getQuantity() + p2.getQuantity(),
                                p1.getPriceperpeice())
                ));

        // Products header
        Table headerTable = new Table(threeColumnWidth);
        headerTable.addCell(createCompactCell("Produkti", true, TextAlignment.LEFT));
        headerTable.addCell(createCompactCell("Sasia", true, TextAlignment.CENTER));
        headerTable.addCell(createCompactCell("Çmimi", true, TextAlignment.RIGHT));
        document.add(headerTable);

        // Product rows
        float total = 0;
        for (Product product : productMap.values()) {
            Table productTable = new Table(threeColumnWidth);
            float productTotal = product.getQuantity() * product.getPriceperpeice();
            total += productTotal;

            productTable.addCell(createCompactCell(product.getPname().orElse(""), false, TextAlignment.LEFT));
            productTable.addCell(createCompactCell(
                    String.format("%d x %.2f", product.getQuantity(), product.getPriceperpeice()),
                    false,
                    TextAlignment.CENTER
            ));
            productTable.addCell(createCompactCell(
                    String.format("%.2f €", productTotal),
                    false,
                    TextAlignment.RIGHT
            ));

            document.add(productTable);
        }

        // Total
        document.add(createDivider());
        Table totalTable = new Table(twoColumnWidth);
        totalTable.addCell(createCompactCell("Totali:", true, TextAlignment.LEFT));
        totalTable.addCell(createCompactCell(String.format("%.2f €", total), true, TextAlignment.RIGHT));
        document.add(totalTable);
        document.add(createDivider());
    }

    private void createCompactTerms(List<String> terms) {
        Table termsTable = new Table(singleColumnWidth);
        for (String term : terms) {
            termsTable.addCell(createCompactCell(term, false, TextAlignment.CENTER));
        }
        document.add(termsTable);
    }

    private Cell createCompactCell(String text, boolean bold, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setFontSize(8))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(alignment);

        if (bold) {
            cell.setBold();
        }

        return cell;
    }

    private Table createDivider() {
        Table divider = new Table(singleColumnWidth);
        divider.setBorder(new DashedBorder(Color.GRAY, 0.5f));
        return divider;
    }
}