package br.ufscar.dc.internship.models;

import java.util.UUID;
import java.math.BigDecimal;

public class Offer
{
    private String id;
    private String type;
    private String side;
    private BigDecimal price;
    private int qty;

    public Offer(String type, String side, String price, int qty)
    {
        id = UUID.randomUUID().toString();
        this.type = type;
        this.side = side;
        this.price = new BigDecimal(price);
        this.qty = qty;
    }

    public String getId()
    {
        return id;
    }
    
    public String getType()
    {
        return type;
    }

    public String getSide()
    {
        return side;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public int getQty()
    {
        return qty;
    }
}