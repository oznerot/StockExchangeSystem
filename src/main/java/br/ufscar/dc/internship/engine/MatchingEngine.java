package br.ufscar.dc.internship.engine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufscar.dc.internship.config.EngineConstants;
import br.ufscar.dc.internship.models.Order;
import br.ufscar.dc.internship.models.OrderBook;
import br.ufscar.dc.internship.models.Trade;
import br.ufscar.dc.internship.utils.Side;
import br.ufscar.dc.internship.utils.Type;

public class MatchingEngine implements EngineConstants
{
    // HashMap para diversos Livros de Ordens.
    public Map<String, OrderBook> orderBookMap;


    // Construtor
    public MatchingEngine()
    {
        orderBookMap = new HashMap<String, OrderBook>();
    }

    /**
     * @param asset - Novo asset que será usado como chave para livro de ordens
     * 
     * @return True se a operação foi realizada com sucesso. False caso contrário
     */
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

    /**
     * Processa uma ordem Limit
     * 
     * @param side - Side da ordem, "buy" ou "sell"
     * @param price - Preço da ordem
     * @param quantity - Quantidade da ordem
     * @param asset - Em qual livro de ordens a ordem será processada
     * 
     * @return True se a operação foi bem sucedidade. False caso contrário.
     */
    public boolean submitLimitOrder(String side, double price, int quantity, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return false;
        }

        Side sideEnum = side.equals("buy") ? Side.BUY : Side.SELL;

        Order order = new Order(Type.LIMIT, sideEnum, new BigDecimal(price), quantity);

        List<Trade> trades = orderBook.matchLimitOrder(order);

        for(Trade trade : trades)
        {
            System.out.println(trade);
        }

        return true;
    }

    /**
     * Processa uma ordem Market
     * 
     * @param side - Side da ordem, "buy" ou "sell"
     * @param quantity - Quantidade da ordem
     * @param asset - Em qual livro de ordens a ordem será processada
     * 
     * @return True caso a operação foi bem sucedida. False caso contrário
     */
    public boolean submitMarketOrder(String side, int quantity, String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return false;
        }

        Side sideEnum = side.equals("buy") ? Side.BUY : Side.SELL;

        Order order = new Order(Type.MARKET, sideEnum, new BigDecimal(0), quantity);

        List<Trade> trades = orderBook.matchMarketOrder(order);

        if(!trades.isEmpty())
        {
            for(Trade trade : trades)
            {
                System.out.println(trade);
            }
        }

        return true;
    }

    /**
     * Cancela uma ordem
     * 
     * @param id - identificador da ordem
     * @param asset - Em qual livro de ordens a ordem será cancelada
     * 
     * @return True se a operação foi bem sucedida. False caso contrário.
     */
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

        orderBook.removeOrderFromBook(order);
        return true;
    }

    /**
     * Atualiza uma ordem
     * 
     * @param id - identificador da ordem
     * @param newPrice - Novo preço da ordem
     * @param newQuantity - Nova quantidade da ordem
     * 
     * @return True se a operação foi bem sucedida. False caso contrário
     */
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
        
        orderBook.removeOrderFromBook(existingOrder);

        BigDecimal price = new BigDecimal(newPrice);

        existingOrder.setPrice(price);
        existingOrder.setQuantity(newQuantity);

        List<Trade> trades = orderBook.matchLimitOrder(existingOrder);

        if(!trades.isEmpty())
        {
            for(Trade trade : trades)
            {
                System.out.println(trade);
            }
        }

        return true;
    }

    public void printBook(String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);

        orderBook.printBook();
    }

    // Método de debug, estava verificando os id no hashmap
    public void viewOrderBookKeys(String asset)
    {
        OrderBook orderBook = orderBookMap.get(asset);
        if(orderBook == null)
        {
            System.out.println(ASSET_NOT_FOUND);
            return;
        }

        System.out.println("Livro de Ordens Keys: " + orderBook.orderMap.keySet());
    }
}