package com.example.demo.controller;

import com.example.demo.dto.InvoiceRequestDto;
import com.example.demo.service_impl.CodingErrorPdfInvoiceCreatorA4Format;
import com.example.demo.service_impl.CodingErrorPdfInvoiceCreatorA6Format;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/invoice")
@CrossOrigin(origins = "*") // Enable CORS for development
public class PdfInvoiceController {

    @PostMapping(value = "/generate-a4", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoiceBig(@RequestBody InvoiceRequestDto request) {
        List<String> termsAndConditions =
                List.of(
                "Ju faleminderit që na zgjodhët!",
                "Mirëseardhëshi!"
        );
        try {
            // Initialize the PDF creator with the desired PDF name
            String pdfName = "invoice_" + request.getHeaderDetails().getInvoiceNo() + ".pdf";
            CodingErrorPdfInvoiceCreatorA4Format pdfCreator = new CodingErrorPdfInvoiceCreatorA4Format(pdfName);

            // Create the document
            pdfCreator.createDocument();

            // Add various sections
            pdfCreator.createHeader(request.getHeaderDetails());
            pdfCreator.createAddress(request.getAddressDetails());
            pdfCreator.createProduct(request.getProducts());
            pdfCreator.createTermsAndConditions(termsAndConditions);

            // Retrieve the PDF as a byte array for response
            byte[] pdfContent = Files.readAllBytes(Paths.get(pdfName));

            // Optionally, delete the temporary PDF file after response
            new File(pdfName).delete();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfName)
                    .body(pdfContent);
        } catch (Exception e) {
            // Handle error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/generate-a6", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateInvoiceSmall(@RequestBody InvoiceRequestDto request) {
        try {
            String pdfName = "invoice_" + request.getHeaderDetails().getInvoiceNo() + ".pdf";
            CodingErrorPdfInvoiceCreatorA6Format pdfCreator = new CodingErrorPdfInvoiceCreatorA6Format(pdfName);

            // Use the single-page method
            pdfCreator.createSinglePageInvoice(
                    request.getHeaderDetails(),
                    request.getAddressDetails(),
                    request.getProducts(),
                    List.of(
                            "Ju faleminderit qe na zgjodhet.",
                            "Ju presim prape."
                    )
            );

            byte[] pdfContent = Files.readAllBytes(Paths.get(pdfName));
            new File(pdfName).delete();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfName)
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}