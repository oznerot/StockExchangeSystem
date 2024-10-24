package br.ufscar.dc.internship.models;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;

import br.ufscar.dc.internship.config.EngineConstants;


public class OrderIndex implements EngineConstants
{
    final Int2IntMap orderIndex = new Int2IntArrayMap(MAX_ORDERS_IN_BOOK / 4);

    public void addIndex(int orderId, Index index)
    {
        orderIndex.put(orderId, index.coalesceValue());

    }

    public Index getIndex(int orderId)
    {
        final int val = orderIndex.remove(orderId);
        return new Index(val);
    }

    public Index removeIndex(int orderId)
    {
        final int val = orderIndex.remove(orderId);
        return new Index(val);
    }

    public static class Index
    {
        int bookPriceLevelIndex;
        int priceLevelOrderIndex;

        public Index(int priceLevelIndex, int orderIndex)
        {
            this.bookPriceLevelIndex = priceLevelIndex;
            this.priceLevel = orderIndex;
        }

        public Index(int val)
        {
            this.bookPriceLevelIndex = Utility.getBookPriceLevelIndex(val);
            this.priceLevel = Utility.getPriceLevelOrderIndex(val);
        }

        public int coalesceValue()
        {
            return Utility.coalescePriceAndOrderLevelIndex(bookPriceLevelIndex, priceLevel);
        }
    }
}