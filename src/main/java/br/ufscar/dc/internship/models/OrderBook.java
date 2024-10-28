package br.ufscar.dc.internship.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufscar.dc.internship.config.EngineConstants;
import br.ufscar.dc.internship.models.Order;
import br.ufscar.dc.internship.models.Trade;
import br.ufscar.dc.internship.utils.Side;

public class OrderBook implements EngineConstants
{
    public Map<String, Order> orderMap;
    public TreeMap<BigDecimal, List<Order>> buyOrders;
    public TreeMap<BigDecimal, List<Order>> sellOrders;

    private List<Order> peggedBuyOrders;
    private List<Order> peggedSellOrders;

    private BigDecimal highestBuyPrice;
    private BigDecimal lowestSellPrice;

    private BigDecimal lastTradedPrice;

    public OrderBook()
    {
        orderMap = new HashMap<>();

        // Ordena os preços de compra de forma decrescente os de venda de forma crescente.
        buyOrders = new TreeMap<>(Collections.reverseOrder());
        sellOrders = new TreeMap<>();

        peggedBuyOrders = new ArrayList<>();
        peggedSellOrders = new ArrayList<>();

        highestBuyPrice = BigDecimal(0);
        lowestSellPrice = BigDecimal(0);

    }

    /**
     * @param id - ID de uma ordem
     * 
     * @return ordem
     */
    public Order getOrder(String id)
    {
        return orderMap.get(id);
    }

    public BigDecimal getLastTradedPrice()
    {
        return lastTradedPrice;
    }

    public BigDecimal getHighestBuyPrice()
    {
        return highestBuyPrice;
    }

    public BigDecimal getLowestSellPrice()
    {
        return lowestSellPrice;
    }

    /**
     * Verifica se já existe um nível de preço no TreeMap,
     * Caso exista, insere a ordem no final da fila,
     * Caso contrário, cria um novo nível de preço e insere a ordem na fila.
     * 
     * Complexidade de tempo: O(log n)
     * 
     * @param order - Ordem que vai ser inserida no TreeMap
     * @param ordersTreeMap - TreeMap, buyOrders ou sellOrders
     */
    private void addOrderToTreeMap(Order order, TreeMap<BigDecimal, List<Order>> ordersTreeMap)
    {
        BigDecimal orderPrice = order.getPrice();

        if(ordersTreeMap.containsKey(orderPrice))
        {
            ordersTreeMap.get(orderPrice).add(order);
        }
        else
        {
            List<Order> newPriceLevel = new ArrayList<>();
            newPriceLevel.add(order);
            ordersTreeMap.put(orderPrice, newPriceLevel);
        }
    }
    /**
     * Verifica à qual side uma ordem pertence,
     * Após isso, chama a função de inserção no TreeMap respectivo,
     * Por fim, adiciona o ID da ordem no HashMap
     * 
     * Complexidade de tempo: O(log n)
     * 
     * @param order - Ordem a ser inserida no Livro
     */
    public void addOrderToBook(Order order)
    {
        BigDecimal orderPrice = order.getPrice();
        String side = order.getSide() == Side.BUY ? "buy" : "sell";

        if(order.getSide() == Side.BUY)
        {
            addOrderToTreeMap(order, buyOrders);
        }
        else
        {
            addOrderToTreeMap(order, sellOrders);
        }

        orderMap.put(order.getId(), order);
        System.out.println("Order created: " + side + " " + order.toString() + " " + order.getId());
    }

    /**
     * Remove uma ordem de determinado nível de preço,
     * Verifica se após a remoção o nível de preço ficou vazio,
     * Se sim, remove o nível de preço do TreeMap
     * 
     * Complexidade de tempo no pior caso (Todas as ordens estão no mesmo nível de preço): O(n)
     * 
     * @param order - Objeto do tipo Ordem que sera removido
     * @param ordersTreeMap - Objeto do tipo TreeMap<BigDecimal, List<Order>>
     */
    private void removeOrderFromTreeMap(Order order, TreeMap<BigDecimal, List<Order>> ordersTreeMap)
    {
        BigDecimal orderPrice = order.getPrice();

        if(!ordersTreeMap.containsKey(orderPrice))
        {
            return;
        }

        List<Order> priceLevel = ordersTreeMap.get(orderPrice);
        priceLevel.remove(order);
        if(priceLevel.isEmpty())
        {
            buyOrders.remove(orderPrice);
        }
    }


    /**
     * Verifica à qual side a ordem pertence e chama o método de remoção respectivo,
     * Por fim, remove a ordem também do HashMap.
     * 
     * Complexidade de tempo: O(n)
     * 
     * @param order - Ordem que será removida
     */
    public void removeOrderFromBook(Order order)
    {
        if(order.getSide() == Side.BUY)
        {
            removeOrderFromTreeMap(order, buyOrders);

        }
        else
        {
            removeOrderFromTreeMap(order, sellOrders);

        }

        orderMap.remove(order.getId());
    }

    /**
     * Busca o melhor preço em determinado side,
     * Caso seja Buy, o melhor preço é o maior valor,
     * Caso seja Sell, o melhor preço é o menor valor.
     * 
     * Complexidade de tempo: O(log n)
     * 
     * @param ordersTreeMap - Objeto do tipo TreeMap<BigDecimal, List<Order>> onde será feita a busca.
     * 
     * @return Maior preço de compra OU Menor preço de venda
     */
    public BigDecimal getBestPriceInSide(TreeMap<BigDecimal, List<Order>> ordersTreeMap)
    {
        BigDecimal bestPrice = ordersTreeMap.firstKey();

        return bestPrice;
    }

    /**
     * Calcula o volume total de ativos em um TreeMap
     * 
     * Complexidade de tempo: O(n)
     * 
     * @param ordersTreeMap - Objeto do tipo TreeMap<BigDecimal, List<Order>>
     * 
     * @return Volume total de ativos
     */
    public int getTotalVolumeInSide(TreeMap<BigDecimal, List<Order>> ordersTreeMap)
    {
        int volume = 0;

        for(List<Order> orders : ordersTreeMap.values())
        {
            for(Order order : orders)
            {
                volume += order.getQuantity();
            }
        }

        return volume;
    }

    /**
     * Computa uma lista com os N melhores preços de um Side,
     * Caso seja BUY, será os N maiores preços,
     * Caso seja SELL, será os N menores preços
     * 
     * Complexidade de tempo: O(MAX_TOP_PRICES) = O(1)
     * 
     * @param ordersTreeMap - Objeto do tipo TreeMap<BigDecimal, List<Order>>
     * 
     * @return Lista de possíveis preços
     */
    public List<BigDecimal> topNBestPricesInSide(TreeMap<BigDecimal, List<Order>> ordersTreeMap)
    {
        List<BigDecimal> prices = new ArrayList<>();
        for(BigDecimal price : ordersTreeMap.keySet())
        {
            if(prices.size() >= MAX_TOP_PRICES)
            {
                break;
            }

            prices.add(price);
        }

        return prices;
    }

    /**
     * Esse método combina uma ordem com outras ordens válidas
     * a verificação das ordens válidas é feita em outros métodos
     * 
     * Pior dos casos: Todas as ordens estão nessa faixa de preço & totalVolume >= incomingOrder.qty
     * Complexidade de tempo: O(n) 
     * 
     * @param validOrders - Lista de ordens válidas
     * @param incomingOrder - Ordem a ser executada
     * @param price - preço do nível de preço atual
     */
    private Trade executeMatch(List<Order> validOrders, Order incomingOrder, BigDecimal price)
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
                orderMap.remove(order.getId()); // Remove a ordem completada do HashMap
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

        // Remove as ordens completadas do nivel de preço
        validOrders.removeIf(n -> (n.getQuantity() == 0));

        return new Trade(price, tradedQty);
    }

    /**
     * Processa uma ordem do tipo Market e combina com as ordens que possam ser feitas
     * 
     * Complexidade de tempo: O(n)
     * 
     * @param incomingOrder - Ordem do tipo Market que vai ser processada
     * @param ordersTreeMap - Objeto do tipo TreeMap<BigDecimal, List<Order>>
     * 
     * @return Lista das trades que foram realizadas.
     */
    private List<Trade> processMarketOrderAndMatchTrades(Order incomingOrder, TreeMap<BigDecimal, List<Order>> ordersTreeMap)
    {
        List<BigDecimal> possiblePrices = topNBestPricesInSide(ordersTreeMap);
        List<Trade> tradesCompleted = new ArrayList<>();
        if(possiblePrices.isEmpty())
        {
            System.out.println("Livro vazio.");
            return tradesCompleted;
        }

        for(BigDecimal price : possiblePrices)
        {
            if(incomingOrder.getQuantity() == 0)
            {
                break;
            }

            List<Order> ordersAtThisPrice = ordersTreeMap.get(price);
            Trade trade = executeMatch(ordersAtThisPrice, incomingOrder, price);
            tradesCompleted.add(trade);
        }

        if(incomingOrder.getQuantity() != 0)
        {
            System.out.println("Ordem processada sofreu um partial fill");
        }



        return tradesCompleted;
    }

    /**
     * Verifica o Side e chama o método para tratar o Side correspondente
     * 
     * Complexidade de tempo: O(n)
     * 
     * @param incomingOrder - Ordem do tipo Market que vai ser processada
     * 
     * @return Lista das trades realizadas
     */
    public List<Trade> matchMarketOrder(Order incomingOrder)
    {
        List<BigDecimal> possiblePrices;
        List<Trade> tradesCompleted = new ArrayList<>();

        if(incomingOrder.getSide() == Side.BUY)
        {
            tradesCompleted = processMarketOrderAndMatchTrades(incomingOrder, sellOrders);

            if(sellOrders.isEmpty())
            {
                lowestSellPrice = lastTradedPrice;
            }
            else
            {
                lowestSellPrice = sellOrders.firstKey();
            }
        }
        else
        {
            tradesCompleted = processMarketOrderAndMatchTrades(incomingOrder, buyOrders);

            if(buyOrders.isEmpty())
            {
                highestSellPrice = lastTradedPrice;
            }
            else
            {
                highestSellPrice = buyOrders.firstKey();
            }

        }

        return tradesCompleted;
    }

    /**
     * Processa uma ordem do tipo Limit e combina com as ordens que possam ser feitas
     * 
     * Complexidade de tempo: O(n)
     * 
     * @param incomingOrder - Ordem do tipo Market que vai ser processada
     * @param ordersTreeMap - Objeto do tipo TreeMap<BigDecimal, List<Order>>
     * @param priceComparison - Objeto do tipo BiPredicate<BigDecimal, BigDecimal>, é o tipo de comparação que será realizado
     * 
     * @return Lista das trades que foram realizadas.
     */
    private List<Trade> processLimitOrderAndMatchTrades(Order incomingOrder,
                                                        TreeMap<BigDecimal, List<Order>> ordersTreeMap,
                                                        BiPredicate<BigDecimal, BigDecimal> priceComparison)
    {
        List<BigDecimal> possiblePrices = topNBestPricesInSide(ordersTreeMap);
        List<Trade> tradesCompleted = new ArrayList<>();
        if(possiblePrices.isEmpty())
        {
            addOrderToBook(incomingOrder);
            System.out.println("Livro de compra vazio. Ordem adicionada no livro.");
            return tradesCompleted;
        }

        for(BigDecimal price : possiblePrices)
        {
            if(priceComparison.test(incomingOrder.getPrice(), price))
            {
                if(incomingOrder.getQuantity() == 0)
                {
                    break;
                }

                List<Order> ordersAtThisPrice = ordersTreeMap.get(price);
                Trade trade = executeMatch(ordersAtThisPrice, incomingOrder, price);
                tradesCompleted.add(trade);
            }
        }

        if(incomingOrder.getQuantity() != 0)
        {
            addOrderToBook(incomingOrder);
        }

        return tradesCompleted;
    }
    /**
     * Verifica o Side e chama o método para tratar o Side correspondente
     * 
     * Complexidade de tempo: O(n)
     * 
     * @param incomingOrder - Ordem do tipo Limit que vai ser processada
     * 
     * @return Lista de trades que foram realizadas.
     */
    public List<Trade> matchLimitOrder(Order incomingOrder)
    {
        List<BigDecimal> possiblePrices;
        List<Trade> tradesCompleted = new ArrayList<>();
        if(incomingOrder.getSide() == Side.BUY)
        {
            processLimitOrderAndMatchTrades(incomingOrder, sellOrders,
                                           (incomingPrice, bookPrice) -> incomingPrice.compareTo(bookPrice) >= 0);

        }
        else
        {
            processLimitOrderAndMatchTrades(incomingOrder, buyOrders,
                                           (incomingPrice, bookPrice) -> incomingPrice.compareTo(bookPrice) <= 0);

        }

        return tradesCompleted;
    }

    /**
     * Retorna todas as ordens de forma ordenada por preço e chegada
     * 
     * Complexidade de tempo: O(n)
     * 
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

    /**
     * Printa o livro em formato tabular
     * 
     * Complexidade de tempo: O(n)
     */
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
        System.out.printf("%20s %20s\n", getTotalVolumeInSide(buyOrders), getTotalVolumeInSide(sellOrders));
        System.out.println("-------------------------------------------------");

    }
}