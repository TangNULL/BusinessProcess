package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DirectController {
    @RequestMapping("/publicChain")
    public String publicChain() {
        return "publicChain";
    }

    @RequestMapping("/registerPage")
    public String register() {
        return "register";
    }

    @RequestMapping("/pcSupervision")
    public String pcSupervision() {
        return "pcSupervision";
    }

    @RequestMapping("/consortiumChain")
    public String consortiumChain() {
        return "consortiumChain";
    }

    @RequestMapping("/bpDetail")
    public String bpDetail() {
        return "bpDetail";
    }

    @RequestMapping("/bpDetailAndProcess")
    public String bpDetailAndProcess() {
        return "bpDetailAndProcess";
    }

    @RequestMapping("/coopRequestPage")
    public String coopRequestPage() {
        return "coopRequestPage";
    }
}
