package com.example.demo.service_impl;

import com.example.demo.model.bill.*;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CodingErrorPdfInvoiceCreatorA4Format {
    Document document;
    PdfDocument pdfDocument;
    String pdfName;
    float threecol=190f;
    float twocol=285f;
    float twocol150=twocol+150f;
    float twocolumnWidth[]={250f, 250f};
    float threeColumnWidth[]={threecol,threecol,threecol};
    float fullwidth[]={threecol*3};

    public CodingErrorPdfInvoiceCreatorA4Format(String pdfName){
        this.pdfName=pdfName;
    }

    public void createDocument() throws FileNotFoundException {
        PdfWriter pdfWriter=new PdfWriter(pdfName);
        pdfDocument=new PdfDocument(pdfWriter);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        this.document=new Document(pdfDocument);
    }

    public void createTermsAndConditions(List<String> tncList) {
        // Remove all margins/padding for precise positioning
        document.setMargins(0, 0, 0, 0);

        // Calculate available space (A6 page height is 297 points)
        float pageHeight = 297f;
        float currentY = document.getRenderer().getCurrentArea().getBBox().getTop();
        float remainingSpace = pageHeight - currentY - 20f; // 20f bottom margin

        // Create centered container with NO borders
        Table container = new Table(1).useAllAvailableWidth();
        container.setBorder(Border.NO_BORDER); // This removes all borders from the table
        container.setFixedPosition(
                document.getLeftMargin(),
                document.getBottomMargin() + 10f, // 10f from bottom
                document.getPdfDocument().getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin()
        );

        // Add centered messages with NO borders
        for (String term : tncList) {
            container.addCell(new Cell()
                    .setBorder(Border.NO_BORDER) // Ensure no cell borders
                    .add(new Paragraph(term)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(8)
                            .setPaddingTop(2f)));
        }

        document.add(container);
        document.close();
    }

    public void createProduct(List<Product> productList) {
        float threecol = 190f;
        float fullwidth[] = {threecol * 3};

        // Add "Products" title with underline
        Paragraph productsTitle = new Paragraph("Produktet")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(5f);
        document.add(productsTitle);

        // Add full width line under title
        document.add(fullwidthDashedBorder(fullwidth).setMarginBottom(15f));

        // Create tables for each column with improved formatting
        Table descriptionTable = new Table(1).setMarginRight(10f);
        Table quantityTable = new Table(1).setMarginRight(10f);
        Table priceTable = new Table(1);

        // Add column headers with consistent padding
        descriptionTable.addCell(new Cell().add("Përshkrimi")
                .setBorder(Border.NO_BORDER)
                .setBold()
                .setPaddingLeft(5f));

        quantityTable.addCell(new Cell().add("Sasia")
                .setBorder(Border.NO_BORDER)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setPaddingLeft(5f));

        priceTable.addCell(new Cell().add("Çmimi")
                .setBorder(Border.NO_BORDER)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingLeft(5f));

        float totalSum = 0f;

        // Add products with consistent formatting
        for (Product product : productList) {
            float total = product.getQuantity() * product.getPriceperpeice();
            totalSum += total;

            descriptionTable.addCell(new Cell().add(product.getPname().orElse(""))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingLeft(5f));

            quantityTable.addCell(new Cell().add(String.format("%d x %.2f €", product.getQuantity(), product.getPriceperpeice()))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPaddingLeft(5f));

            priceTable.addCell(new Cell().add(String.format("%.2f", total))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setPaddingLeft(5f));
        }

        // Create container table
        Table containerTable = new Table(new float[]{threecol, threecol, threecol})
                .setMarginBottom(15f);
        containerTable.addCell(new Cell().add(descriptionTable).setBorder(Border.NO_BORDER));
        containerTable.addCell(new Cell().add(quantityTable).setBorder(Border.NO_BORDER));
        containerTable.addCell(new Cell().add(priceTable).setBorder(Border.NO_BORDER));

        document.add(containerTable);

        // Add total section with improved formatting
        document.add(fullwidthDashedBorder(fullwidth).setMarginTop(15f).setMarginBottom(10f));

        Table totalTable = new Table(new float[]{threecol * 2, threecol});
        totalTable.addCell(new Cell().add("Totali")
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(20f));

        totalTable.addCell(new Cell().add(String.format("%.2f €", totalSum))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(totalTable);
        document.add(fullwidthDashedBorder(fullwidth).setMarginTop(10f));

        // Add spacing before terms
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
    }

    public float getTotalSum(List<Product> productList) {
        return  (float)productList.stream().mapToLong((p)-> (long) (p.getQuantity()*p.getPriceperpeice())).sum();
    }

    public List<Product> getDummyProductList()
    {
        List<Product> productList=new ArrayList<>();
        productList.add(new Product("apple",2,159));
        productList.add(new Product("mango",4,205));
        productList.add(new Product("banana",2,90));
        productList.add(new Product("grapes",3,10));
        productList.add(new Product("apple",5,159));
        productList.add(new Product("kiwi",2,90));
        return productList;
    }

    public void createTableHeader(ProductTableHeader productTableHeader) {
        Paragraph producPara=new Paragraph("Products");
        document.add(producPara.setBold());
        Table threeColTable1=new Table(threeColumnWidth);
        threeColTable1.setBackgroundColor(Color.BLACK,0.7f);

        threeColTable1.addCell(new Cell().add("Description").setBold().setFontColor(Color.WHITE).setBorder(Border.NO_BORDER));
        threeColTable1.addCell(new Cell().add("Quantity").setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        threeColTable1.addCell(new Cell().add("Price").setBold().setFontColor(Color.WHITE).setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER)).setMarginRight(15f);
        document.add(threeColTable1);
    }

    public void createAddress(AddressDetails addressDetails) {
        // Header with better spacing
        Table twoColTable = new Table(twocolumnWidth);
        twoColTable.addCell(getBillingandShippingCell(addressDetails.getBillingInfoText())
                .setPaddingBottom(8f));  // Added bottom padding
        document.add(twoColTable.setMarginBottom(12f));

        // Business/Client Info - First Row
        Table twoColTable2 = new Table(twocolumnWidth);
        twoColTable2.addCell(getCell10fLeft("Biznesi", true));  // Hardcoded for consistency
        twoColTable2.addCell(getCell10fLeft("Lëshoi", true));  // Changed from "Leshoi"
        twoColTable2.addCell(getCell10fLeft(addressDetails.getBillingCompany(), false));
        twoColTable2.addCell(getCell10fLeft(addressDetails.getBillingName(), false));
        document.add(twoColTable2.setMarginBottom(5f));  // Added spacing

        // Address/Contact - Second Row
        Table twoColTable3 = new Table(twocolumnWidth);
        twoColTable3.addCell(getCell10fLeft("Adresa", true));  // Simplified label
        twoColTable3.addCell(getCell10fLeft("Kontakti", true));
        twoColTable3.addCell(getCell10fLeft(addressDetails.getBillingAddress(), false));
        twoColTable3.addCell(getCell10fLeft(addressDetails.getBillingEmail(), false));
        document.add(twoColTable3.setMarginBottom(5f));  // Added spacing

        // City/ID - Third Row
        Table twoColTable4 = new Table(twocolumnWidth);
        twoColTable4.addCell(getCell10fLeft("Qyteti, Kodi Postar", true));
        twoColTable4.addCell(getCell10fLeft("Nr. Unik Identifikues", true));  // Simplified label
        twoColTable4.addCell(getCell10fLeft("GJILAN 60000", false));  // Assuming new field
        twoColTable4.addCell(getCell10fLeft(addressDetails.getShippingAddress(), false));
        document.add(twoColTable4.setMarginBottom(10f));

        document.add(fullwidthDashedBorder(fullwidth));
    }

    public void createHeader(HeaderDetails header) {
        Table table = new Table(twocolumnWidth)
                .setMarginBottom(15f);

        // Invoice title with better spacing
        table.addCell(new Cell().add(header.getInvoiceTitle())
                .setFontSize(20f)
                .setBorder(Border.NO_BORDER)
                .setBold()
                .setPaddingBottom(10f));

        // Nested table with fixed column widths
        Table nestedTable = new Table(new float[]{120f, 120f})
                .setMarginTop(5f);

        // Invoice number row
        nestedTable.addCell(getHeaderTextCell(header.getInvoiceNoText())
                .setPaddingBottom(3f));
        nestedTable.addCell(getHeaderTextCellValue(header.getInvoiceNo())
                .setPaddingBottom(3f));

        // Invoice date row
        nestedTable.addCell(getHeaderTextCell(header.getInvoiceDateText())
                .setPaddingBottom(3f));
        nestedTable.addCell(getHeaderTextCellValue(header.getInvoiceDate())
                .setPaddingBottom(3f));

        // Invoice date row
        nestedTable.addCell(getHeaderTextCell(header.getInvoiceTimeText())
                .setPaddingBottom(3f));
        nestedTable.addCell(getHeaderTextCellValue(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .setPaddingBottom(3f));

        table.addCell(new Cell().add(nestedTable)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.BOTTOM));

        document.add(table);
        document.add(getNewLineParagraph());

        // Divider with consistent styling
        Border gb = new SolidBorder(header.getBorderColor(), 1.5f);  // Thinner border
        document.add(getDividerTable(fullwidth).setBorder(gb));
        document.add(getNewLineParagraph().setMarginBottom(15f));  // Added more space
    }


    public List<Product> modifyProductList(List<Product> productList) {
        Map<String,Product> map=new HashMap<>();
        productList.forEach((i)->{
            if(map.containsKey(i.getPname().orElse("")))
            {
                i.setQuantity(map.getOrDefault(i.getPname().orElse(""),null).getQuantity()+i.getQuantity());
                map.put(i.getPname().orElse(""),i);
            }else
            {
                map.put(i.getPname().orElse(""),i);
            }
        });
        return map.values().stream().collect(Collectors.toList());


    }

    static  Table getDividerTable(float[] fullwidth)
    {
        return new Table(fullwidth);
    }
    static Table fullwidthDashedBorder(float[] fullwidth)
    {
        Table tableDivider2=new Table(fullwidth);
        Border dgb=new DashedBorder(Color.GRAY,0.5f);
        tableDivider2.setBorder(dgb);
        return tableDivider2;
    }
    static  Paragraph getNewLineParagraph()
    {
        return new Paragraph("\n");
    }
    static Cell getHeaderTextCell(String textValue)
    {

        return new Cell().add(textValue).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
    }
    static Cell getHeaderTextCellValue(String textValue)
    {


        return new Cell().add(textValue).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }
    static Cell getBillingandShippingCell(String textValue)
    {

        return new Cell().add(textValue).setFontSize(12f).setBold().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
    }

    static  Cell getCell10fLeft(String textValue,Boolean isBold){
        Cell myCell=new Cell().add(textValue).setFontSize(10f).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
        return  isBold ?myCell.setBold():myCell;

    }
}