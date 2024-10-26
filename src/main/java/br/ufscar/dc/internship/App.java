package br.ufscar.dc.internship;

import br.ufscar.dc.internship.engine.MatchingEngine;

public class App {
    public static void main(String[] args) {
        String asset = "Apple";

        MatchingEngine engine = new MatchingEngine();

        engine.newAsset(asset);

        // Buy Orders <type, side, price, qty>
        engine.queryOrder("limit buy 1004 10", asset);
        engine.queryOrder("limit buy 1003 25", asset);
        engine.queryOrder("limit buy 1002 50", asset);
        engine.queryOrder("limit buy 1001 40", asset);
        engine.queryOrder("limit buy 1000 120", asset);

        // Sell Orders
        engine.queryOrder("limit sell 1005 20", asset);
        engine.queryOrder("limit sell 1006 50", asset);
        engine.queryOrder("limit sell 1007 30", asset);
        engine.queryOrder("limit sell 1008 100", asset);
        engine.queryOrder("limit sell 1009 210", asset);


        engine.printBook(asset);

        engine.queryOrder("limit buy 1005 25", asset);
        engine.queryOrder("limit sell 1002 100", asset);
        
        engine.printBook(asset);
    }
}
