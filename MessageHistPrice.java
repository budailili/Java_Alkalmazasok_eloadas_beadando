package com.example.beadando.forex;

public class MessageHistPrice {
    private String instrument = "EUR_USD";
    private String granularity = "D";

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }
}
