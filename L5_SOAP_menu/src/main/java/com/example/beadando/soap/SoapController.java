package com.example.beadando.soap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SoapController {

    private final MnbService mnbService;

    public SoapController(MnbService mnbService) {
        this.mnbService = mnbService;
    }

    @GetMapping("/soap")
    public String soapForm(Model model) {
        MessageExchangeRate param = new MessageExchangeRate();
        param.setCurrency("EUR");
        param.setEndDate(LocalDate.now().toString());
        param.setStartDate(LocalDate.now().minusDays(30).toString());
        fillModel(model, param);
        return "soap/soap";
    }

    @PostMapping("/soap")
    public String soapResult(@ModelAttribute("form") MessageExchangeRate param, Model model) {
        fillModel(model, param);

        String error = validate(param);
        if (error != null) {
            model.addAttribute("error", error);
            return "soap/soap";
        }

        try {
            List<ExchangeRate> rates = mnbService.getExchangeRates(param.getCurrency(), param.getStartDate(), param.getEndDate());
            if (rates.isEmpty()) {
                model.addAttribute("error", "A megadott időszakra nincs árfolyamadat (pl. hétvége vagy ünnepnap).");
                return "soap/soap";
            }

            List<String> labels = new ArrayList<>();
            List<Double> values = new ArrayList<>();
            double min = rates.get(0).getValue();
            double max = rates.get(0).getValue();
            double sum = 0;
            for (ExchangeRate r : rates) {
                labels.add(r.getDate());
                values.add(r.getValue());
                if (r.getValue() < min) {
                    min = r.getValue();
                }
                if (r.getValue() > max) {
                    max = r.getValue();
                }
                sum += r.getValue();
            }

            model.addAttribute("rates", rates);
            model.addAttribute("labels", labels);
            model.addAttribute("values", values);
            model.addAttribute("unit", rates.get(0).getUnit());
            model.addAttribute("min", min);
            model.addAttribute("max", max);
            model.addAttribute("avg", sum / rates.size());
        } catch (Exception e) {
            model.addAttribute("error", "Nem sikerült lekérni az adatokat az MNB-től: " + e.getMessage());
        }
        return "soap/soap";
    }

    private void fillModel(Model model, MessageExchangeRate param) {
        model.addAttribute("menu", "soap");
        model.addAttribute("form", param);
        model.addAttribute("currencies", mnbService.getCurrencies());
        model.addAttribute("today", LocalDate.now().toString());
    }

    private String validate(MessageExchangeRate param) {
        if (param.getStartDate() == null || param.getStartDate().isEmpty()
                || param.getEndDate() == null || param.getEndDate().isEmpty()) {
            return "Add meg a kezdő és a befejező dátumot is.";
        }
        if (param.getStartDate().compareTo(param.getEndDate()) > 0) {
            return "A kezdő dátum nem lehet későbbi a befejező dátumnál.";
        }
        return null;
    }
}
