package br.ufscar.dc.internship.models;

import java.math.BigDecimal;

import br.ufscar.dc.internship.utils.Side;
import br.ufscar.dc.internship.utils.Type;

public class Order
{
    Type type;
    Side side;
    BigDecimal price;
    int quantity;

    /**
     * @param type - LIMIT ou MARKET
     * @param side - BUY ou SELL
     * @param price - preço da ordem
     * @param quantity - quantidade da ordem
     */
    public Order(Type type, Side side, BigDecimal price, int quantity)
    {
        this.type = type;
        this.side = side;
        this.price = price;
        this.quantity = quantity;

    }

    /**
     * @return preço da ordem
     */
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * @return quantity - quantidade da ordem
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * @return side - BUY ou SELL
     */
    public Side getSide()
    {
        return side;
    }

    /** 
     * @return LIMIT ou MARKET
     */
    public Type getType()
    {
        return type;
    }

    /**
     * @param quantity - nova quantidade da ordem
     */
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    /**
     * @return String formatada
     */
    @Override
    public String toString()
    {
        return quantity + " @ " + price;
    }
}