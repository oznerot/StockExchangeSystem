package br.ufscar.dc.internship.models;

import java.util.ArrayList;
import java.util.List;

import br.ufscar.dc.internship.models.Order;

public class PricePoint
{
    private List<Order> orders;

    public PricePoint()
    {
        orders = new ArrayList<>();
    }
}