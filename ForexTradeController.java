package com.example.beadando.forex;

import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.transaction.OrderFillTransaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ForexTradeController {

    private final ForexService forexService;

    public ForexTradeController(ForexService forexService) {
        this.forexService = forexService;
    }

    // ---------- Forex-Nyit ----------

    @GetMapping("/forex/open")
    public String openForm(Model model) {
        model.addAttribute("submenu", "open");
        model.addAttribute("form", new MessageOpenPosition());
        return "forex/open";
    }

    @PostMapping("/forex/open")
    public String open(@ModelAttribute("form") MessageOpenPosition form, Model model) {
        model.addAttribute("submenu", "open");
        if (!ForexService.contains(ForexService.INSTRUMENTS, form.getInstrument())) {
            model.addAttribute("error", "Ismeretlen instrumentum.");
            return "forex/open";
        }
        if (form.getUnits() == null || form.getUnits() == 0) {
            model.addAttribute("error", "A mennyiség nem lehet 0. Pozitív szám: Long (vétel), negatív szám: Short (eladás).");
            return "forex/open";
        }
        try {
            OrderCreateResponse response = forexService.openPosition(form.getInstrument(), form.getUnits());
            OrderFillTransaction fill = response.getOrderFillTransaction();
            if (fill == null || fill.getTradeOpened() == null) {
                String reason = response.getOrderCancelTransaction() != null
                        ? String.valueOf(response.getOrderCancelTransaction().getReason())
                        : "ismeretlen ok";
                model.addAttribute("error", "A megbízás nem teljesült: " + reason);
                return "forex/open";
            }
            model.addAttribute("fill", fill);
            model.addAttribute("tradeId", fill.getTradeOpened().getTradeID());
            model.addAttribute("direction", form.getUnits() > 0 ? "Long" : "Short");
        } catch (Exception e) {
            model.addAttribute("error", "A pozíció megnyitása nem sikerült: " + ForexService.errorMessage(e));
        }
        return "forex/open";
    }

    // ---------- Forex-Poz ----------

    @GetMapping("/forex/positions")
    public String positions(Model model) {
        model.addAttribute("submenu", "positions");
        if (!forexService.isConfigured()) {
            return "forex/positions";
        }
        try {
            model.addAttribute("trades", forexService.getOpenTrades());
        } catch (Exception e) {
            model.addAttribute("error", "A nyitott pozíciók lekérése nem sikerült: " + ForexService.errorMessage(e));
        }
        return "forex/positions";
    }
}
