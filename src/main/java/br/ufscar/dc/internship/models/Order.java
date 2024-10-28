package br.ufscar.dc.internship.models;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import br.ufscar.dc.internship.utils.Side;
import br.ufscar.dc.internship.utils.Type;

public class Order
{
    private static final AtomicInteger count = new AtomicInteger(0);
    private final String id;
    private final Type type;
    private Side side;
    private BigDecimal price;
    private int quantity;

    /**
     * @param type - LIMIT ou MARKET
     * @param side - BUY ou SELL
     * @param price - preço da ordem
     * @param quantity - quantidade da ordem
     */
    public Order(Type type, Side side, BigDecimal price, int quantity)
    {
        this.id = "identificador_" + count.incrementAndGet();
        this.type = type;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * @return id - identificador da ordem
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return preço da ordem
     */
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * @return quantidade da ordem
     */
    public int getQuantity()
    {
        return quantity;
    }

    /**
     * @return BUY ou SELL
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
     * @param price - novo preço da ordem
     */
    public void setPrice(BigDecimal price)
    {
        this.price = price;
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