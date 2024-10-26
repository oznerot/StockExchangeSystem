package br.ufscar.dc.internship.models;

import java.math.BigDecimal;

public class Trade
{
    private BigDecimal price;
    private int quantity;

    public Trade(BigDecimal price, int quantity)
    {
        this.price = price;
        this.quantity = quantity;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public int getQuantity()
    {
        return quantity;
    }

    @Override
    public String toString()
    {
        return "Trade, price: " + price + ", quantity: " + quantity;
    }
}