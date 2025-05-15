package com.example.demo.controller;

import com.example.demo.dto.Invoice;
import com.example.demo.model.bill.AddressDetails;
import com.example.demo.model.bill.HeaderDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoice")
public class HomeController {

    @GetMapping
    public String home(Model model) {
        // Create and add an empty invoice object to the model
        Invoice invoice = new Invoice();
        invoice.setHeaderDetails(new HeaderDetails());
        invoice.setAddressDetails(new AddressDetails());
        model.addAttribute("invoice", invoice);
        return "index";
    }

}
