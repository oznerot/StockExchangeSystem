package br.ufscar.dc.internship.engine;

import br.ufscar.dc.internship.models.Order;
import br.ufscar.dc.internship.models.OrderIndex;
import br.ufscar.dc.internship.models.OrderBook;
import br.ufscar.dc.internship.models.Trade;

public class Engine
{
    private void matchAndInsert(OrderBook crossBook, int orderId, Order order,
                                OrderBook orderBook, OrderIndex orderIndex)
    {
        final List<Trade> trades = crossBook.matchOrder(orderId, order);
        if(!trades.isEmpty())
        {
            execute(orderId, order.getSide(), trades);
        }
        if(!order.isComplete())
        {
            final OrderIndex.Index buyIndex = orderBook.onNewOrder(orderId, order);
            orderIndex.addIndex(orderId, buyIndex);
        }
    }
}