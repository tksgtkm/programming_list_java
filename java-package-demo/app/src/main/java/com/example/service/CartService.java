package com.example.service;

import com.example.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private List<Product> items = new ArrayList<>();

    public void addProduct(Product product) {
        items.add(product);
        System.out.println(" [追加] " + product.getName() + " をカートに追加しました。");
    }

    public int calculateTotal() {
        return items.stream().mapToInt(Product::getPrice).sum();
    }

    public void printCart() {
        System.out.println("\n  --- カートの中身 ---");
        items.forEach(p -> System.out.printf("  ・%s : %d円%n", p.getName(), p.getPrice()));
        System.out.println("  合計: " + calculateTotal() + "円");
        System.out.println("  -------------------");
    }
}
