package com.example.beadando.forex;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForexAccountController {

    private final ForexService forexService;

    public ForexAccountController(ForexService forexService) {
        this.forexService = forexService;
    }

    @GetMapping("/forex/account")
    public String account(Model model) {
        model.addAttribute("submenu", "account");
        if (!forexService.isConfigured()) {
            return "forex/account";
        }
        try {
            model.addAttribute("account", forexService.getAccountSummary());
        } catch (Exception e) {
            model.addAttribute("error", "A számlainformációk lekérése nem sikerült: " + ForexService.errorMessage(e));
        }
        return "forex/account";
    }
}
