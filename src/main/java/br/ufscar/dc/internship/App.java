package br.ufscar.dc.internship;

import java.util.Scanner;

import br.ufscar.dc.internship.engine.MatchingEngine;

public class App {
    public static void main(String[] args) {

        //insertionTest();
        testeUm();
        
        //peggedToBidTest();

        //peggedToOfferTest();
    }

    private static void insertionTest()
    {
        
        MatchingEngine engine = new MatchingEngine();
        String asset = "a";
        engine.newAsset(asset);

        for (int i = 0; i < 1000; i++) {
            System.out.println((i % 2 == 0 ? "limit buy " : "limit sell ") + (i + 1) + " 1");
            engine.submitLimitOrder(i % 2 == 0 ? "buy" : "sell", String.valueOf(i + 1), 1, asset);
        }

        engine.printBook(asset);
    }

    private static void testeUm()
    {
        MatchingEngine engine = new MatchingEngine();
        String asset = "aaa";
        engine.newAsset(asset);

        engine.submitLimitOrder("buy", "1004", 25, asset);
        engine.submitLimitOrder("buy", "1004", 40, asset);
        engine.submitLimitOrder("buy", "1003", 125, asset);
        engine.submitLimitOrder("buy", "1002", 100, asset);
        engine.submitLimitOrder("buy", "1001", 150, asset);

        engine.submitLimitOrder("sell", "1005", 10, asset);
        engine.submitLimitOrder("sell", "1006", 50, asset);
        engine.submitLimitOrder("sell", "1007", 25, asset);
        engine.submitLimitOrder("sell", "1008", 150, asset);
        engine.submitLimitOrder("sell", "1009", 120, asset);

        engine.printBook(asset);

        engine.submitLimitOrder("buy", "1005", 10, asset);
        engine.submitMarketOrder("sell", 100, asset);
        engine.submitLimitOrder("buy", "1008", 40, asset);
        engine.submitLimitOrder("sell", "1004", 20, asset);
        engine.cancelOrder("identificador_5", asset);
        engine.updateOrder("identificador_10", "1005", 200, asset);
        engine.printBook(asset);
    }
}
