package com.jeju.backend;

public class Main {
    public static void main(String[] args) {
        System.out.println("Backend placeholder running...");
        while (true) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ignored) {}
        }
    }
}