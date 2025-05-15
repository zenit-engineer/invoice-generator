package com.example.demo.dto;

import com.example.demo.model.bill.AddressDetails;
import com.example.demo.model.bill.HeaderDetails;
import com.example.demo.model.bill.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceRequestDto {
    private HeaderDetails headerDetails;
    private AddressDetails addressDetails;
    private List<Product> products;
//    private boolean lastPage;
//    private String imagePath;
    private String invoiceId;
}
