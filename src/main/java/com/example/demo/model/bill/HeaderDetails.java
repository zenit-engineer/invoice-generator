package com.example.demo.model.bill;

import com.example.demo.deserializer.ColorDeserializer;
import com.example.demo.util.ConstantUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itextpdf.kernel.color.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HeaderDetails {
    String invoiceTitle= ConstantUtil.INVOICE_TITLE;
    String invoiceNoText=ConstantUtil.INVOICE_NO_TEXT;
    String invoiceDateText=ConstantUtil.INVOICE_DATE_TEXT;
    String invoiceTimeText=ConstantUtil.INVOICE_TIME_TEXT;
    String invoiceNo= ConstantUtil.EMPTY;
    String invoiceDate=ConstantUtil.EMPTY;

    @JsonDeserialize(using = ColorDeserializer.class) // <-- Here you add the annotation
    Color borderColor=Color.GRAY;

    public HeaderDetails setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
        return this;
    }

    public HeaderDetails setInvoiceNoText(String invoiceNoText) {
        this.invoiceNoText = invoiceNoText;
        return this;
    }

    public HeaderDetails setInvoiceDateText(String invoiceDateText) {
        this.invoiceDateText = invoiceDateText;
        return this;
    }

    public HeaderDetails setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
        return this;
    }

    public HeaderDetails setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
        return this;
    }

    public HeaderDetails setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }
    public HeaderDetails build()
    {
        return  this;
    }

}