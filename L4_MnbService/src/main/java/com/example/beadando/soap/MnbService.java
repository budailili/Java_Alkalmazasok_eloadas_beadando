package com.example.beadando.soap;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapImpl;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MnbService {

    private static final String[] DEFAULT_CURRENCIES = {"EUR", "USD", "GBP", "CHF", "JPY", "CZK", "PLN"};

    private List<String> currencies;

    private MNBArfolyamServiceSoap getService() {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        return impl.getCustomBindingMNBArfolyamServiceSoap();
    }

    // A devizalistát egyszer kérjük le, utána a memóriából adjuk vissza.
    public List<String> getCurrencies() {
        if (currencies != null) {
            return currencies;
        }
        List<String> result = new ArrayList<>();
        try {
            Document doc = parse(getService().getCurrencies());
            NodeList nodes = doc.getElementsByTagName("Curr");
            for (int i = 0; i < nodes.getLength(); i++) {
                String code = nodes.item(i).getTextContent().trim();
                if (!code.equals("HUF")) {
                    result.add(code);
                }
            }
            currencies = result;
        } catch (Exception e) {
            // ha az MNB nem elérhető, egy alap listával dolgozunk tovább
            result.clear();
            for (String c : DEFAULT_CURRENCIES) {
                result.add(c);
            }
        }
        return result;
    }

    public List<ExchangeRate> getExchangeRates(String currency, String startDate, String endDate) throws Exception {
        String xml = getService().getExchangeRates(startDate, endDate, currency);
        Document doc = parse(xml);

        List<ExchangeRate> rates = new ArrayList<>();
        NodeList days = doc.getElementsByTagName("Day");
        for (int i = 0; i < days.getLength(); i++) {
            Element day = (Element) days.item(i);
            NodeList rateNodes = day.getElementsByTagName("Rate");
            if (rateNodes.getLength() == 0) {
                continue;
            }
            Element rate = (Element) rateNodes.item(0);
            int unit = Integer.parseInt(rate.getAttribute("unit"));
            // az MNB tizedesvesszővel küldi az értéket
            double value = Double.parseDouble(rate.getTextContent().trim().replace(',', '.'));
            rates.add(new ExchangeRate(day.getAttribute("date"), unit, value));
        }

        // időrendbe tesszük (régebbi elöl), hogy a grafikon balról jobbra haladjon
        if (rates.size() > 1 && rates.get(0).getDate().compareTo(rates.get(rates.size() - 1).getDate()) > 0) {
            List<ExchangeRate> ordered = new ArrayList<>();
            for (int i = rates.size() - 1; i >= 0; i--) {
                ordered.add(rates.get(i));
            }
            rates = ordered;
        }
        return rates;
    }

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }
}
