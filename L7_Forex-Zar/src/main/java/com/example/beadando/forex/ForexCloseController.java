package com.example.beadando.forex;

import com.oanda.v20.trade.TradeCloseResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ForexCloseController {

    private final ForexService forexService;

    public ForexCloseController(ForexService forexService) {
        this.forexService = forexService;
    }

    @GetMapping("/forex/close")
    public String closeForm(Model model) {
        model.addAttribute("form", new MessageClosePosition());
        prepare(model);
        return "forex/close";
    }

    @PostMapping("/forex/close")
    public String close(@ModelAttribute("form") MessageClosePosition form, Model model) {
        if (form.getTradeId() == null || form.getTradeId() <= 0) {
            model.addAttribute("error", "Adj meg egy érvényes tradeId-t.");
            prepare(model);
            return "forex/close";
        }
        try {
            TradeCloseResponse response = forexService.closeTrade(form.getTradeId());
            model.addAttribute("closedId", form.getTradeId());
            if (response.getOrderFillTransaction() != null) {
                model.addAttribute("fill", response.getOrderFillTransaction());
            }
            form.setTradeId(null);
        } catch (Exception e) {
            model.addAttribute("error", "A(z) " + form.getTradeId() + " azonosítójú pozíció zárása nem sikerült: "
                    + ForexService.errorMessage(e));
        }
        prepare(model);
        return "forex/close";
    }

    // segítségként kilistázzuk a még nyitott trade-eket
    private void prepare(Model model) {
        model.addAttribute("submenu", "close");
        if (!forexService.isConfigured()) {
            return;
        }
        try {
            model.addAttribute("trades", forexService.getOpenTrades());
        } catch (Exception e) {
            // a lista csak segítség, ha nem jön le, a zárás attól még működik
        }
    }
}
