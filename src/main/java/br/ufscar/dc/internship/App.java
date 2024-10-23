package br.ufscar.dc.internship;

import br.ufscar.dc.internship.models.Offer;

public class App {
    public static void main(String[] args) {
        Offer offer = new Offer("limit", "buy", "10.90", 100);

        System.out.println("Offer ID: " + offer.getId());
        System.out.println("Offer Type: " + offer.getType());
        System.out.println("Offer Side: " + offer.getSide());
        System.out.println("Offer Price: " + offer.getPrice());
        System.out.println("Offer Quantity: " + offer.getQty());
    }
}
