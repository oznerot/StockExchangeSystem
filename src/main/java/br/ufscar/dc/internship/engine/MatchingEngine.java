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
    public void newAsset(String asset)
    {
        if(orderBookMap.containsKey(asset))
        {
            throw new RuntimeException("Ativo já registrado");
        }

        OrderBook orderBook = new OrderBook();
        orderBookMap.put(asset, orderBook);
    }

    public void queryOrder(String query, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        String[] tokens = query.split(" ");

        Type type = tokens[0].toLowerCase().equals("limit") ? Type.LIMIT : Type.MARKET;
        Side side = tokens[1].toLowerCase().equals("buy") ? Side.BUY : Side.SELL;
        
        BigDecimal price = new BigDecimal(tokens[2]);
        
        int quantity = Integer.parseInt(tokens[3]);

        Order order = new Order(type, side, price, quantity);

        if(orderBook == null)
        {
            throw new RuntimeException("Ativo não existente");
        }

        orderBook.matchLimitOrder(order);
    }

    public void printBook(String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);

        orderBook.printBook();
    }
}