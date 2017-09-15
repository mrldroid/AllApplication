package com.example;

public class VisibilityDemo {
    private static boolean shutdown = false;
    private static int i ;
    
    static class HelloThread extends Thread{
        @Override
        public void run() {
            while(!shutdown){
                System.out.println("shutdown = "+shutdown);
            }
            System.out.println("exit hello");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new HelloThread().start();
        Thread.sleep(1000);
        shutdown = true;
        i++;
        System.out.println("exit main");

    }
}