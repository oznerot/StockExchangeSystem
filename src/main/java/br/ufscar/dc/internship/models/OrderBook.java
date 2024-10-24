package br.ufscar.dc.internship.models;

import br.ufscar.dc.internship.config.EngineConstants;
import br.ufscar.dc.internship.models.Order;

public class OrderBook implements EngineConstants
{
    final Side side;
    int start = -1;
    int end = -1;
    final PriceLevel[] priceLevels = new PriceLevel[MAX_PRICE_LEVELS];
    final DoubleToIntRBTreeMap priceLevelIndexMap;

    /*
        Construtor

        @param side: Determina se esse Ordem Book pertence ao lado de compra ou venda
    */
    public OrderBook(Side side)
    {
        this.side = side;
        this.priceLevelIndexMap = new DoubleToIntRBTreeMap(new PriceLevelComparator(side));
        init();
    }

    /*
        Inicializa cada elemento em priceLevels com uma instância de PriceLevel
    */
    private void init()
    {
        for (int i = 0; i < MAX_PRICE_LEVELS; i++)
        {
            priceLevels[i] = new PriceLevel(side);
        }
    }

    private void reset()
    {
        start = -1;
        end = -1;
    }

    public List<Trade> matchOrder(int orderId, Order order)
    {
        /*  
            Verifica se a Ordem passada é a
            mesma do livro a ser comparado
        */
        if(side == Side.BUY)
        {
            if(order.isBuy()) return Collections.emptyList();
        }
        else
        {
            if(!order.isBuy()) return Collections.emptyList();
        }

        //  Verifica se há ordem no livro
        if(priceLevelIndexMap.isEmpty())
        {
            return Collections.emptyList();
        }
        
        final List<Trade> trades = new ArrayList<>();

        do
        {
            final Double2IntMap.Entry entry = priceLevelIndexMap.double2IntEntrySet().first();
            final double price = entry.getDoubleKey();
            final int index = entry.getIntValue();

            if(!order.canExecute(price))
            {
                return trades;
            }

            final PriceLevel priceLevel = priceLevel[index];
            priceLevel.matchOrder(orderId, order, trades);

            if(priceLevel.isEmpty())
            {
                priceLevelIndexMap.remove(price);
            }

            if(order.isComplete())
            {
                return trades;
            }
        }
        while(!priceLevelIndexMap.isEmpty());

        return trades;
    }

    public OrderIndex.Index onNewOrder(int orderId, Order order)
    {
        final double price = order.getPrice();
        final int priceLevelIndex = priceLevelIndexMap.getOrDefault(price, -1);
        if(priceLevelIndex != -1)
        {
            return newOrder(orderId, order, priceLevelIndex);
        }
        else
        {
            if(start == -1)
            {
                start = 0;
                end = 0;
                
                int index = start;
                priceLevelIndexMap.put(price, index);
                return newOrder(orderId, order, index);
            }

            final int index = moduloPowerOfTwo(end + 1, MAX_PRICE_LEVELS);

            if(index == start)
            {
                throw new RuntimeException("Todos os espacos nesse nivel foram ocupados");
            }

            end = index;
            priceLevelIndexMap.put(price, index);
            return newOrder(orderId, order, index);
        }
    }
    
    private OrderIndex.Index newOrder(int orderId, Order order, int priceLevelIndex)
    {
        final PriceLevel priceLevel = priceLevels[priceLevelIndex];
        final int orderIndex = priceLevel.newOrder(orderId, order);
        return new OrderIndex.Index(priceLevelIndex, orderIndex);
    }
}