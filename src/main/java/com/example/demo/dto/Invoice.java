package com.example.demo.dto;

import com.example.demo.model.bill.AddressDetails;
import com.example.demo.model.bill.HeaderDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Invoice {
    private HeaderDetails headerDetails;
    private AddressDetails addressDetails;
    private String invoiceId;
    private boolean lastPage;
    private String imagePath;
    private String termsAndConditions;

    // getters and setters
}

