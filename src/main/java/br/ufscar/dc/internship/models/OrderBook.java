package br.ufscar.dc.internship.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufscar.dc.internship.config.EngineConstants;
import br.ufscar.dc.internship.models.Order;
import br.ufscar.dc.internship.utils.Side;

public class OrderBook implements EngineConstants
{
    public Map<String, Order> orderMap;
    public TreeMap<BigDecimal, List<Order>> buyOrders;
    public TreeMap<BigDecimal, List<Order>> sellOrders;

    BigDecimal lastTradedPrice;

    public OrderBook()
    {
        orderMap = new HashMap<>();
        buyOrders = new TreeMap<>(Collections.reverseOrder());
        sellOrders = new TreeMap<>();


        lastTradedPrice = new BigDecimal(0);
    }

    /**
     * @param id - ID de uma ordem
     * @return ordem
     */
    public Order getOrder(String id)
    {
        return orderMap.get(id);
    }

    /**
     * @param order - Ordem a ser inserida no Livro
     */
    public void addOrderToBook(Order order)
    {
        BigDecimal orderPrice = order.getPrice();
        String side = order.getSide() == Side.BUY ? "buy" : "sell";

        if(order.getSide() == Side.BUY)
        {
            if(buyOrders.containsKey(orderPrice))
            {
                buyOrders.get(orderPrice).add(order);
            }
            else
            {
                List<Order> priceLevel = new ArrayList<>();
                priceLevel.add(order);
                buyOrders.put(orderPrice, priceLevel);
            }
        }
        else
        {
            if(sellOrders.containsKey(orderPrice))
            {
                sellOrders.get(orderPrice).add(order);
            }
            else
            {
                List<Order> priceLevel = new ArrayList<>();
                priceLevel.add(order);
                sellOrders.put(orderPrice, priceLevel);
            }
        }

        orderMap.put(order.getId(), order);
        System.out.println("Order created: " + side + " " + order.toString() + " " + order.getId());
    }

    /**
     * Esse método recebe uma ordem para ser removida tanto do TreeMap respectivo
     * quanto do HashMap<id, ordem>
     * 
     * @param order - Ordem que será removida
     * 
     */
    public void removeOrder(Order order)
    {
        BigDecimal orderPrice = order.getPrice();
        if(order.getSide() == Side.BUY)
        {
            List<Order> priceLevel = buyOrders.get(orderPrice);
            priceLevel.remove(order);
            if(priceLevel.isEmpty())
            {
                buyOrders.remove(orderPrice);
            }
        }
        else
        {
            List<Order> priceLevel = sellOrders.get(orderPrice);
            priceLevel.remove(order);
            if(priceLevel.isEmpty())
            {
                sellOrders.remove(orderPrice);
            }
        }

        orderMap.remove(order.getId());
    }

    /**
     * @return Maior preço de compra
     */
    public BigDecimal getBestBuyPrice()
    {
        BigDecimal bestBuyPrice = buyOrders.firstKey();

        return bestBuyPrice;
    }

    /**
     * @return Menor preço de venda
     */
    public BigDecimal getBestSellPrice()
    {
        BigDecimal bestSellPrice = sellOrders.firstKey();

        return bestSellPrice;
    }

    /**
     * @return volume - Quantidade total de um ativo para venda
     */
    public int getBuyVolume()
    {
        int volume = 0;

        for (List<Order> orders : buyOrders.values())
        {
            for(Order order : orders)
            {
                volume += order.getQuantity();
            }
        }

        return volume;
    }

    /**
     * @return volume - Quantidade total de um ativo para venda
     */
    public int getSellVolume()
    {
        int volume = 0;

        for(List<Order> orders : sellOrders.values())
        {
            for(Order order : orders)
            {
                volume += order.getQuantity();
            }
        }

        return volume;
    }

    /**
     * @return prices - Lista com até os 5 melhores preços de compra
     */
    public List<BigDecimal> topNBestBuyPrices()
    {
        List<BigDecimal> prices = new ArrayList<>();
        for(BigDecimal price : buyOrders.keySet())
        {
            if(prices.size() < MAX_TOP_PRICES)
            {
                prices.add(price);
            }
            else
            {
                break;
            }
        }

        return prices;
    }

    /**
     * @return prices - Lista com até os 5 melhores preços de venda
     */
    public List<BigDecimal> topNBestSellPrices()
    {
        int n = 5;

        List<BigDecimal> prices = new ArrayList<>();
        for(BigDecimal price : sellOrders.keySet())
        {
            if(prices.size() < n)
            {
                prices.add(price);
            }
            else
            {
                break;
            }
        }

        return prices;
    }

    /**
     * Esse método combina uma ordem com outras ordens válidas
     * a verificação das ordens válidas é feita em outros métodos
     * 
     * @param validOrders - Lista de ordens válidas
     * @param incomingOrder - Ordem a ser executada
     * @param price - preço do nível de preço atual
     */
    private void executeMatch(List<Order> validOrders, Order incomingOrder, BigDecimal price)
    {
        int tradedQty = 0;
        for(Order order : validOrders)
        {
            int orderQty = order.getQuantity();
            int incomingOrderQty = incomingOrder.getQuantity();

            if(orderQty < incomingOrderQty)
            {
                tradedQty += orderQty;
                incomingOrder.setQuantity(incomingOrderQty - orderQty);
                order.setQuantity(0);
                orderMap.remove(order.getId()); // Remove a ordem completada do HashMap

            }
            else if(orderQty == incomingOrderQty)
            {
                tradedQty += incomingOrderQty;
                order.setQuantity(0);
                incomingOrder.setQuantity(0);
                orderMap.remove(order.getId(    )); // Remove a ordem completada do HashMap
                break;
            }
            else
            {
                tradedQty += incomingOrderQty;
                order.setQuantity(orderQty - incomingOrderQty);
                incomingOrder.setQuantity(0);

                break;
            }
        }

        lastTradedPrice = price;

        // Remove as ordens completadas do nivel de preço
        validOrders.removeIf(n -> (n.getQuantity() == 0));

        System.out.println("Trade, price: " + price + ", qty: " + tradedQty);
    }

    /**
     * @param incomingOrder - Ordem do tipo Market que vai ser processada
     */
    public void matchMarketOrder(Order incomingOrder)
    {
        List<BigDecimal> possiblePrices;
        if(incomingOrder.getSide() == Side.BUY)
        {
            possiblePrices = topNBestSellPrices();
            if(!possiblePrices.isEmpty())
            {
                for(BigDecimal price : possiblePrices)
                {
                    if(incomingOrder.getQuantity() == 0)
                    {
                        break;
                    }

                    List<Order> ordersAtThisPrice = sellOrders.get(price);
                    executeMatch(ordersAtThisPrice, incomingOrder, price);
                }

                if(incomingOrder.getQuantity() != 0)
                {
                    System.out.println("Ordem processada sofreu um partial fill");
                }
            }
            else
            {
                System.out.println("Livro de venda vazio");
            }
        }
        else
        {
            possiblePrices = topNBestBuyPrices();
            if(!possiblePrices.isEmpty())
            {
                for(BigDecimal price : possiblePrices)
                {
                    if(incomingOrder.getQuantity() == 0)
                    {
                        break;
                    }

                    List<Order> ordersAtThisPrice = buyOrders.get(price);
                    executeMatch(ordersAtThisPrice, incomingOrder, price);
                }

                if(incomingOrder.getQuantity()!= 0)
                {
                    System.out.println("Ordem processada sofreu um partial fill");
                }
            }
            else
            {
                System.out.println("Livro de compra vazio");
            }
        }
    }

    /**
     * @param incomingOrder - Ordem do tipo Limit que vai ser processada
     */
    public void matchLimitOrder(Order incomingOrder)
    {
        List<BigDecimal> possiblePrices;
        if(incomingOrder.getSide() == Side.BUY)
        {
            possiblePrices = topNBestSellPrices();
            if(!possiblePrices.isEmpty())
            {
                for(BigDecimal price : possiblePrices)
                {
                    if(incomingOrder.getPrice().compareTo(price) >= 0)
                    {
                        if(incomingOrder.getQuantity() == 0)
                        {
                            break;
                        }

                        List<Order> ordersAtThisPrice = sellOrders.get(price);
                        executeMatch(ordersAtThisPrice, incomingOrder, price);
                    }
                }

                if(incomingOrder.getQuantity() != 0)
                {
                    addOrderToBook(incomingOrder);
                }
            }
            else
            {
                addOrderToBook(incomingOrder);
            }
        }
        else
        {
            possiblePrices = topNBestBuyPrices();
            if(!possiblePrices.isEmpty())
            {
                for(BigDecimal price : possiblePrices)
                {
                    if(incomingOrder.getPrice().compareTo(price) <= 0)
                    {
                        if(incomingOrder.getQuantity() == 0)
                        {
                            break;
                        }

                        List<Order> ordersAtThisPrice = buyOrders.get(price);
                        executeMatch(ordersAtThisPrice, incomingOrder, price);
                    }
                }

                if(incomingOrder.getQuantity()!= 0)
                {
                    addOrderToBook(incomingOrder);
                }
            }
            else
            {
                addOrderToBook(incomingOrder);
            }
        }
    }

    /**
     * @param priceMap - é um TreeMap que mapeia níveis de preço para sua lista correspondente
     * @return lista ordenada por preço de todas as ordens de determinado Side
     */
    public List<Order> getAllOrders(TreeMap<BigDecimal, List<Order>> priceMap)
    {
        List<Order> allOrders = new ArrayList<>();

        for(List<Order> orders : priceMap.values())
        {
            for(Order order : orders)
            {
                allOrders.add(order);
            }
        }

        return allOrders;
    }

    public void printBook()
    {
        System.out.println("-------------------------------------------------");
        System.out.printf("%20s %20s\n", "BUY", "SELL");
        System.out.println("-------------------------------------------------");
        
        List<Order> allBuys = getAllOrders(buyOrders);
        List<Order> allSells = getAllOrders(sellOrders);

        int buySize = allBuys.size();
        int sellSize = allSells.size();

        int n = Math.max(buySize, sellSize);

        String buyPrint = "";
        String sellPrint = "";

        for(int i = 0; i < n; i++)
        {
            if(i >= buySize)
            {
                buyPrint = "";
            }
            else
            {
                buyPrint = allBuys.get(i).toString();
            }
            if(i >= sellSize)
            {
                sellPrint = "";
            }
            else
            {
                sellPrint = allSells.get(i).toString();
            }

            System.out.format("%20s %20s\n", buyPrint, sellPrint);

        }
        System.out.println("-------------------------------------------------");
        System.out.printf("%20s %20s\n", getBuyVolume(), getSellVolume());
        System.out.println("-------------------------------------------------");

    }
}