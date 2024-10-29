package br.ufscar.dc.internship;

import java.util.Scanner;

import br.ufscar.dc.internship.engine.MatchingEngine;

public class App {
    public static void main(String[] args) {

        //insertionTest();

        System.out.println("============================================================");

        //peggedToBidTest();

        //peggedToOfferTest();
    }

    private static void insertionTest()
    {
        
        MatchingEngine engine = new MatchingEngine();
        engine.newAsset("Test");

        for (int i = 0; i < 20; i++) {
            System.out.println((i % 2 == 0? "limit buy " : "limit sell ") + (i + 1) + " 1");
            engine.submitLimitOrder(i % 2 == 0? "buy" : "sell", String.valueOf(i + 1), 1, "Test");
        }

        engine.printBook("Test");
    }

    private static void peggedToBidTest()
    {
        String asset = "Apple";

        MatchingEngine engine = new MatchingEngine();

        engine.newAsset(asset);

        System.out.println("limit buy 10 200");
        engine.submitLimitOrder("buy", "10", 200, asset);

        System.out.println();

        System.out.println("limit buy 9.99 100");
        engine.submitLimitOrder("buy", "9.99", 100, asset);

        System.out.println();

        System.out.println("limit sell 10.5 100");
        engine.submitLimitOrder("sell", "10.5", 100, asset);

        System.out.println();

        engine.printBook(asset);

        System.out.println();

        System.out.println("peg bid buy 150");
        engine.submitPeggedToBidOrder("buy", 150, asset);

        System.out.println();

        engine.printBook(asset);

        System.out.println();

        System.out.println("limit buy 10.1 300");
        engine.submitLimitOrder("buy", "10.1", 300, asset);

        System.out.println();

        engine.printBook(asset);

        System.out.println();

    }

    private static void peggedToOfferTest()
    {
        String asset = "Apple";

        MatchingEngine engine = new MatchingEngine();

        engine.newAsset(asset);

        System.out.println("limit sell 10 100");
        engine.submitLimitOrder("sell", "10.5", 100, asset);

        System.out.println();

        System.out.println("limit sell 9.99 100");
        engine.submitLimitOrder("sell", "9.99", 100, asset);

        System.out.println();

        System.out.println("limit buy 10.5 100");
        engine.submitLimitOrder("buy", "9", 100, asset);

        System.out.println();

        engine.printBook(asset);

        System.out.println();

        System.out.println("peg offer sell 150");
        engine.submitPeggedToOfferOrder("sell", 150, asset);

        System.out.println();

        engine.printBook(asset);
    }

    private void tradeTest()
    {
        String asset = "Apple";

        MatchingEngine engine = new MatchingEngine();

        engine.newAsset(asset);

        // ID: identificador_1
        System.out.println("limit buy 10 100");
        engine.submitLimitOrder("buy", "10", 100, asset);

        // ID: identificador_2
        System.out.println("limit sell 20 200");
        engine.submitLimitOrder("sell", "20", 200, asset);

        // ID: identificador_3
        System.out.println("limit sell 20 200");
        engine.submitLimitOrder("sell", "20", 200, asset);

        // ID: identificador_4
        // Vai realizar um trade completo com a ordem identificador_2
        // Quantidade da ordem identificador_2 vai ser igual a 50
        System.out.println("market buy 150");
        engine.submitMarketOrder("buy", 150, asset);

        // ID: identificador_5
        // Vai realizar um trade com 50 unidades da ordem identificador_2
        // e 150 unidades da ordem identificador_3, sobrando 50 unidades de
        // identificador_3
        System.out.println("market buy 200");
        engine.submitMarketOrder("buy", 200, asset);

        // Teremos ordem de compra identificador_1, quantidade == 100
        // e ordem de venda identificador_3, quantidade == 50
        engine.printBook(asset);

        engine.updateOrder("identificador_3", "20.01", 500, asset);

        engine.printBook(asset);

    }
}
