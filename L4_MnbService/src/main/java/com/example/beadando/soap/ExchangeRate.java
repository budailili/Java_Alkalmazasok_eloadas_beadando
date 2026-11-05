package com.example.beadando.soap;

public class ExchangeRate {
    private final String date;
    private final int unit;
    private final double value;

    public ExchangeRate(String date, int unit, double value) {
        this.date = date;
        this.unit = unit;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public int getUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }
}
