public class Utility implements EngineConstants
{

    //  Operação de modulo dado divisor seja potencia de 2
    public static int moduloPowerOfTwo(int v, int d)
    {
        return v & (d - 1);
    }

    //  Combina o Índice do Nível de Preço com o Índica da Ordem no Nível de Preço questão
    //  para gerar um valor único.
    public static int coalescePriceAndOrderLevelIndex(int bookPriceLevelIndex, int priceLevelOrderIndex)
    {
        final int val = (bookPriceLevelIndex << 15) | (priceLevelOrderIndex);
        return val;
    }

    //  Retorna o Índice do Nível de Preço dado o valor combinado
    public static int getPriceLevelOrderIndex(int coalescedVal)
    {
        return coalescedVal & (MAX_ORDERS_AT_EACH_PRICE_LEVEL_INDEX - 1);
    }

    //  Retorna o Índice da Ordem no Nível de Preço dado o valor combinado
    public static int getBookPriceLevelIndex(int coalescedVal)
    {
        return (coalescedVal >> 15) & (MAX_PRICE_LEVELS - 1);
    }
}