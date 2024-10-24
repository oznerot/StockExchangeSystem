package br.ufscar.dc.internship.models;

import br.ufscar.dc.internship.config.EngineConstants;
import br.ufscar.dc.internship.models.Side;

public class PriceLevel implements EngineConstants
{
    double price;
    final Side side;
    int totalOpenOrders = 0;
    int totalCancelOrders = 0;
    int totalExecuteOrders = 0;
    int start = -1;
    int end = -1;

    int[][] orderQty = new int[MAX_ORDERS_AT_EACH_PRICE_LEVEL][2];

    /*
        Construtor

        @param side: Determina se esse nível de preço pertence ao lado de compra ou venda
    */
    public PriceLevel(Side side)
    {
        this.side = side;
    }

    /*
        Verifica se existe ordens disponíveis nesse Nível de Preço

        @return: false caso existam ordens disponíveis, true caso contrário
    */
    public boolean isEmpty()
    {
        return totalOpenOrders == 0;
    }

    /*
        Redefine o Nível de Preço para um novo valor

        @param price: novo valor do Nível de Preço
    */
    private void reset(double price)
    {
        this.price = price;
        this.totalOpenOrders = 0;
        this.totalCancelOrders = 0;
        this.totalExecuteOrders = 0;
        this.start = -1;
        this.end = -1;
    }

    /*
        Invalida uma Ordem específica, dado índice

        @param orderIndex: índice da Ordem
    */
    private void makeInvalid(int orderIndex)
    {
        orderQty[orderIndex][0] = -1;
        orderQty[orderIndex][1] = -1;
        totalOpenOrders--;
    }

    /*
        Verifica se uma Ordem específica é válida

        @param orderIndex: índice da Ordem
        @return: true caso a Ordem seja válida, false caso contrário
    */
    private boolean isInvalid(int orderIndex)
    {
        return orderQuantity[orderIndex][0] <= 0 && orderQuantity[orderIndex][1] <= 0;
    }

    public double getPrice()
    {
        return price;
    }

    public int newOrder(int orderId, Order order)
    {
        if(totalOpenOrders == 0)
        {
            reset(order.getPrice());
        }

        if(start == -1)
        {
            start = 0;
            int index = start;
            insertOrder(orderId, order, index);
            return index;
        }

        final int index = moduloPowerOfTwo(end + 1, MAX_ORDERS_AT_EACH_PRICE_LEVEL);

        if(index == start)
        {
            throw new RuntimeException("Todos os espaços nesse nível foram ocupados");
        }

        insertOrder(orderId, order, index);
        return index;
    }

    private void insertOrder(int orderId, Order order, int index)
    {
        orderQuantity[index][0] = orderId;
        orderQuantity[index][1] = order.getQuantity();
        end = index;
        totalOpenOrders++;
    }
}