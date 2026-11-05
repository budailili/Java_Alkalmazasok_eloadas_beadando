package com.example.beadando.forex;

public class MessageOpenPosition {
    private String instrument = "EUR_USD";
    private Integer units = 100;

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }
}
