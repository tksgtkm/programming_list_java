package com.example.app;

import com.example.model.Product;
import com.example.service.CartService;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Javaパッケージ サンプル ===");

        Product apple = new Product("りんご", 150);
        Product milk = new Product("牛乳", 200);
        Product bread = new Product("食パン", 180);

        CartService cart = new CartService();
        cart.addProduct(apple);
        cart.addProduct(milk);
        cart.addProduct(bread);

        cart.printCart();

        System.out.println("\n=== 完了 ===");
    }
}
