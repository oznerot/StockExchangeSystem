package br.ufscar.dc.internship;

import br.ufscar.dc.internship.models.Offer;
import br.ufscar.dc.internship.models.OfferBook;

public class App {
    public static void main(String[] args) {
        Offer offer = new Offer("limit", "buy", "10.50", 100);

        System.out.println(offer.toString());

        OfferBook book = new OfferBook();

        for(int i = 0; i < 5; i++)
        {
            book.addToBook(new Offer("limit", "buy", String.valueOf(10.50 + 0.50 * i), 100));
            book.addToBook(new Offer("limit", "sell",String.valueOf(10.50 + 0.50 * i), 100));
        }

        System.out.println("Buy List:");
        while(!book.buyList.isEmpty())
        {
            System.out.println(book.buyList.poll());
        }

        System.out.println("Sell List:");
        while(!book.sellList.isEmpty())
        {
            System.out.println(book.sellList.poll());
        }
    }
}
