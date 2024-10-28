package br.ufscar.dc.internship.engine;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import br.ufscar.dc.internship.models.Order;
import br.ufscar.dc.internship.models.OrderBook;
import br.ufscar.dc.internship.utils.Side;
import br.ufscar.dc.internship.utils.Type;

public class MatchingEngine
{
    // HashMap para diversos Livros de Ordens.
    public Map<String, OrderBook> orderBookMap;


    // Construtor
    public MatchingEngine()
    {
        orderBookMap = new HashMap<String, OrderBook>();
    }

    // Adiciona um novo ativo na Engine
    public boolean newAsset(String asset)
    {
        if(orderBookMap.containsKey(asset))
        {
            System.out.println(ASSET_ALREADY_EXISTS);
            return false;
        }

        OrderBook orderBook = new OrderBook();
        orderBookMap.put(asset, orderBook);

        return true;
    }

    public boolean limitOrder(String query, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return false;
        }

        String[] tokens = query.split(" ");
        Side side = tokens[0].toLowerCase().equals("buy") ? Side.BUY : Side.SELL;
        BigDecimal price = new BigDecimal(tokens[1]);
        int quantity = Integer.parseInt(tokens[2]);

        Order order = new Order(Type.LIMIT, side, price, quantity);

        orderBook.matchLimitOrder(order);

        return true;
    }

    public boolean marketOrder(String query, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return false;
        }

        String[] tokens = query.split(" ");
        Side side = tokens[0].toLowerCase().equals("buy")? Side.BUY : Side.SELL;
        int quantity = Integer.parseInt(tokens[1]);

        Order order = new Order(Type.MARKET, side, new BigDecimal(0), quantity);

        orderBook.matchMarketOrder(order);

        return true;
    }

    public boolean cancelOrder(String id, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return false;
        }

        Order order = orderBook.getOrder(id);
        if(order == null)
        {
            System.out.println(ORDER_NOT_FOUND);
            return false;
        }

        orderBook.removeOrder(order);
        return true;
    }

    public boolean updateOrder(String id, String newPrice, int newQuantity, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return false;
        }

        Order existingOrder = orderBook.getOrder(id);
        if(existingOrder == null)
        {
            System.out.println("Ordem não encontrada");
            return false;
        }

        orderBook.removeOrder(existingOrder);

        BigDecimal price = new BigDecimal(newPrice);

        existingOrder.setPrice(price);
        existingOrder.setQuantity(newQuantity);

        orderBook.matchLimitOrder(existingOrder);

        return true;
    }

    public void printBook(String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);

        orderBook.printBook();
    }

    public void viewOrderBookKeys(String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_MOT_FOUND);
            return;
        }

        System.out.println("Livro de Ordens Keys: " + orderBook.orderMap.keySet());
    }
}