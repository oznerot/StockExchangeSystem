package br.ufscar.dc.internship.models;

import java.math.BigDecimal;

public class Trade
{
    BigDecimal price;
    int quantity;

    public Trade(BigDecimal price, int quantity)
    {
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public String toString()
    {
        return "Trade, price: " + price + ", qty: " + quantity;
    }
}