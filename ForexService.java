package com.example.beadando.forex;

import com.oanda.v20.Context;
import com.oanda.v20.RequestException;
import com.oanda.v20.account.AccountSummary;
import com.oanda.v20.instrument.Candlestick;
import com.oanda.v20.instrument.CandlestickGranularity;
import com.oanda.v20.instrument.InstrumentCandlesRequest;
import com.oanda.v20.order.MarketOrderRequest;
import com.oanda.v20.order.OrderCreateRequest;
import com.oanda.v20.order.OrderCreateResponse;
import com.oanda.v20.pricing.ClientPrice;
import com.oanda.v20.pricing.PricingGetRequest;
import com.oanda.v20.primitives.InstrumentName;
import com.oanda.v20.trade.Trade;
import com.oanda.v20.trade.TradeCloseRequest;
import com.oanda.v20.trade.TradeCloseResponse;
import com.oanda.v20.trade.TradeSpecifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ForexService {

    public static final String[] INSTRUMENTS = {
            "EUR_USD", "GBP_USD", "USD_JPY", "USD_CHF", "AUD_USD", "USD_CAD", "EUR_GBP", "EUR_JPY"
    };

    public static final String[] GRANULARITIES = {"M1", "M5", "M15", "M30", "H1", "H4", "D", "W", "M"};

    private final OandaConfig config;
    private Context ctx;

    public ForexService(OandaConfig config) {
        this.config = config;
    }

    public boolean isConfigured() {
        return config.isConfigured();
    }

    private Context getContext() {
        if (ctx == null) {
            ctx = new Context(config.getUrl(), config.getToken());
        }
        return ctx;
    }

    public ClientPrice getActualPrice(String instrument) throws Exception {
        List<String> instruments = new ArrayList<>();
        instruments.add(instrument);
        PricingGetRequest request = new PricingGetRequest(config.getAccountId(), instruments);
        List<ClientPrice> prices = getContext().pricing.get(request).getPrices();
        if (prices.isEmpty()) {
            throw new IllegalStateException("Az Oanda nem adott vissza árat ehhez az instrumentumhoz.");
        }
        return prices.get(0);
    }

    public List<Candlestick> getHistoricalPrices(String instrument, String granularity) throws Exception {
        InstrumentCandlesRequest request = new InstrumentCandlesRequest(new InstrumentName(instrument));
        request.setGranularity(CandlestickGranularity.valueOf(granularity));
        request.setCount(10L);
        return getContext().instrument.candles(request).getCandles();
    }

    public OrderCreateResponse openPosition(String instrument, int units) throws Exception {
        MarketOrderRequest marketOrder = new MarketOrderRequest();
        marketOrder.setInstrument(new InstrumentName(instrument));
        marketOrder.setUnits(units);

        OrderCreateRequest request = new OrderCreateRequest(config.getAccountId());
        request.setOrder(marketOrder);
        return getContext().order.create(request);
    }

    public List<Trade> getOpenTrades() throws Exception {
        return getContext().trade.listOpen(config.getAccountId()).getTrades();
    }

    public AccountSummary getAccountSummary() throws Exception {
        return getContext().account.summary(config.getAccountId()).getAccount();
    }

    public TradeCloseResponse closeTrade(long tradeId) throws Exception {
        TradeCloseRequest request = new TradeCloseRequest(config.getAccountId(), new TradeSpecifier(String.valueOf(tradeId)));
        return getContext().trade.close(request);
    }

    public static boolean contains(String[] array, String value) {
        for (String s : array) {
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }

    // Az Oanda hibaüzenete a RequestException-ben van, a többinél a sima üzenetet adjuk vissza.
    public static String errorMessage(Exception e) {
        if (e instanceof RequestException re && re.getErrorMessage() != null) {
            return re.getErrorMessage();
        }
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }
}
