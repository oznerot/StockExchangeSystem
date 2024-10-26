package br.ufscar.dc.internship.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufscar.dc.internship.models.Order;
import br.ufscar.dc.internship.utils.Side;

public class OrderBook
{
    public TreeMap<BigDecimal, List<Order>> buyOrders;
    public TreeMap<BigDecimal, List<Order>> sellOrders;

    public OrderBook()
    {
        buyOrders = new TreeMap<>(Collections.reverseOrder());
        sellOrders = new TreeMap<>();
    }

    /*
     * @param order - Ordem a ser inserida no Livro
     */
    public void addOrderToBook(Order order)
    {
        BigDecimal orderPrice = order.getPrice();

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
    }

    public void removeOrderFromBook(Order order)
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
    }


    /*TODO
    public void cancelOrder(Order order)
    {
        BigDecimal orderPrice = order.getPrice();
        if(order.getSide() == Side.BUY)
        {
            List<Order> priceLevel = buyOrders.get(orderPrice);
            priceLevel.remove(order);
        }
        else
        {
            List<Order> priceLevel = sellOrders.
        }
    }
    */

    public BigDecimal getBestBuyPrice()
    {
        BigDecimal bestBuyPrice = buyOrders.firstKey();

        return bestBuyPrice;
    }

    public BigDecimal getBestSellPrice()
    {
        BigDecimal bestSellPrice = sellOrders.firstKey();

        return bestSellPrice;
    }

    /*
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


    /*
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

    public BigDecimal getMarketPrice(Side side)
    {
        if(side == Side.BUY) return getBestBuyPrice();
        else return getBestSellPrice();
    }

    /*
     * @return prices - Lista com até os 5 melhores preços de compra
     */
    public List<BigDecimal> topNBestBuyPrices()
    {
        int n = 5;

        List<BigDecimal> prices = new ArrayList<>();
        for(BigDecimal price : buyOrders.keySet())
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

    /*
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

    /*
     * @param validOrders - Lista de ordens válidas
     * @param incomingOrder - Ordem a ser executada
     */
    public void executeMatch(List<Order> validOrders, Order incomingOrder)
    {
        for(Order order : validOrders)
        {
            int orderQty = order.getQuantity();
            int incomingOrderQty = incomingOrder.getQuantity();

            if(orderQty < incomingOrderQty)
            {
                System.out.println("ENTREI AQUI PORRA");
                incomingOrder.setQuantity(incomingOrderQty - orderQty);
                order.setQuantity(0);
                //removeOrderFromBook(order);
            }
            else if(orderQty == incomingOrderQty)
            {
                order.setQuantity(0);
                //removeOrderFromBook(order);
                incomingOrder.setQuantity(0);
                break;
            }
            else
            {
                order.setQuantity(orderQty - incomingOrderQty);
                incomingOrder.setQuantity(0);
                break;
            }
        }
    }

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
                    executeMatch(ordersAtThisPrice, incomingOrder);
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
                    if(incomingOrder.getQuantity() == 0)
                    {
                        break;
                    }

                    List<Order> ordersAtThisPrice = buyOrders.get(price);
                    executeMatch(ordersAtThisPrice, incomingOrder);
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
                        executeMatch(ordersAtThisPrice, incomingOrder);
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
                        executeMatch(ordersAtThisPrice, incomingOrder);
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



    public void printBook()
    {
        /*
        System.out.println("================================================");
        System.out.println("|          BUY         |         SIDE          |");
        System.out.println("================================================");
        */

        System.out.println("Buy List:");
        System.out.println("Total Volume: " + getBuyVolume());

        for(List<Order> buyOrder : buyOrders.values())
        {
            for(Order o : buyOrder)
            {
            System.out.println(o.toString());
            }
        }

        System.out.println("Sell List:");
        System.out.println("Total Volume: " + getSellVolume());
        for(List<Order> sellOrder : sellOrders.values())
        {
            for(Order i : sellOrder)
            {
                System.out.println(i.toString());
            }
        }

        System.out.println("================================================");

        List<BigDecimal> topNBuyPrices = topNBestBuyPrices();
        System.out.println("Top " + topNBuyPrices.size() + " buy prices:");
        for(BigDecimal buyPrice :  topNBuyPrices)
        {
            System.out.println(buyPrice);
        }
    
        List<BigDecimal> topNSellPrices = topNBestSellPrices();
        System.out.println("Top " + topNSellPrices.size() + " sell prices:");
        for(BigDecimal sellPrice :  topNSellPrices)
        {
            System.out.println(sellPrice);
        }
    }
}