package br.ufscar.dc.internship;

import java.util.Scanner;

import br.ufscar.dc.internship.engine.MatchingEngine;

public class App {
    public static void main(String[] args) {
        String asset = "Apple";

        MatchingEngine engine = new MatchingEngine();

        engine.newAsset(asset);

        // ID: identificador_1
        System.out.println("limit buy 10 100");
        engine.submitLimitOrder("buy", 10, 100, asset);

        // ID: identificador_2
        System.out.println("limit sell 20 200");
        engine.submitLimitOrder("sell", 20, 200, asset);

        // ID: identificador_3
        System.out.println("limit sell 20 200");
        engine.submitLimitOrder("sell", 20, 200, asset);

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

        engine.updateOrder("identificador_3", "9.99", 500, asset);

        engine.printBook(asset);
    }
}
