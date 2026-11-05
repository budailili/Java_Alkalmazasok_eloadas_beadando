package com.example.beadando.forex;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * A Forex oldalak közös modell-adatai: instrumentum- és granularity-lista,
 * valamint hogy be van-e állítva az Oanda hozzáférés.
 */
@ControllerAdvice(basePackages = "com.example.beadando.forex")
public class ForexModelAdvice {

    private final ForexService forexService;

    public ForexModelAdvice(ForexService forexService) {
        this.forexService = forexService;
    }

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("menu", "forex");
        model.addAttribute("instruments", ForexService.INSTRUMENTS);
        model.addAttribute("granularities", ForexService.GRANULARITIES);
        model.addAttribute("oandaConfigured", forexService.isConfigured());
    }
}
