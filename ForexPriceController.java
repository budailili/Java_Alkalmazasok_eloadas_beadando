package com.example.beadando.forex;

import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.pricing.ClientPrice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ForexPriceController {

    private final ForexService forexService;

    public ForexPriceController(ForexService forexService) {
        this.forexService = forexService;
    }

    // ---------- Forex-AktÁr ----------

    @GetMapping("/forex/actual-price")
    public String actualPriceForm(Model model) {
        model.addAttribute("submenu", "actual");
        model.addAttribute("form", new MessageActPrice());
        return "forex/actual_price";
    }

    @PostMapping("/forex/actual-price")
    public String actualPrice(@ModelAttribute("form") MessageActPrice form, Model model) {
        model.addAttribute("submenu", "actual");
        if (!ForexService.contains(ForexService.INSTRUMENTS, form.getInstrument())) {
            model.addAttribute("error", "Ismeretlen instrumentum.");
            return "forex/actual_price";
        }
        try {
            ClientPrice price = forexService.getActualPrice(form.getInstrument());
            if (price.getBids().isEmpty() || price.getAsks().isEmpty()) {
                model.addAttribute("error", "Ehhez az instrumentumhoz jelenleg nincs jegyzett ár (zárva lehet a piac).");
                return "forex/actual_price";
            }
            double bid = price.getBids().get(0).getPrice().doubleValue();
            double ask = price.getAsks().get(0).getPrice().doubleValue();
            model.addAttribute("price", price);
            model.addAttribute("bid", bid);
            model.addAttribute("ask", ask);
            model.addAttribute("mid", (bid + ask) / 2);
            model.addAttribute("spread", ask - bid);
        } catch (Exception e) {
            model.addAttribute("error", "Az aktuális ár lekérése nem sikerült: " + ForexService.errorMessage(e));
        }
        return "forex/actual_price";
    }

    // ---------- Forex-HistÁr ----------

    @GetMapping("/forex/hist-price")
    public String histPriceForm(Model model) {
        model.addAttribute("submenu", "hist");
        model.addAttribute("form", new MessageHistPrice());
        return "forex/hist_price";
    }

    @PostMapping("/forex/hist-price")
    public String histPrice(@ModelAttribute("form") MessageHistPrice form, Model model) {
        model.addAttribute("submenu", "hist");
        if (!ForexService.contains(ForexService.INSTRUMENTS, form.getInstrument())
                || !ForexService.contains(ForexService.GRANULARITIES, form.getGranularity())) {
            model.addAttribute("error", "Érvénytelen instrumentum vagy granularity.");
            return "forex/hist_price";
        }
        try {
            List<Candlestick> candles = forexService.getHistoricalPrices(form.getInstrument(), form.getGranularity());
            // a legfrissebb ár legyen felül
            List<Candlestick> newestFirst = new ArrayList<>();
            for (int i = candles.size() - 1; i >= 0; i--) {
                newestFirst.add(candles.get(i));
            }
            model.addAttribute("candles", newestFirst);
        } catch (Exception e) {
            model.addAttribute("error", "A historikus árak lekérése nem sikerült: " + ForexService.errorMessage(e));
        }
        return "forex/hist_price";
    }
}
