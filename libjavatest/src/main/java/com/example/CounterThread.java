package com.example;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterThread extends Thread {
    private static int counter = 0;
    private static volatile int counter2 = 0;
    private static AtomicInteger integer = new AtomicInteger(0);
    private static Switcher switcher = new Switcher();
    private int ii;
    public CounterThread(int i) {
        ii = i;
    }

    @Override
    public void run() {
        try {
            Thread.sleep((int)(Math.random()*100));
        } catch (InterruptedException e) {
        }
        counter ++;
        counter2++;
        switcher.setOn(ii);
        integer.getAndIncrement();
    }


    public static void main(String[] args) throws InterruptedException {
        int num = 1000;
        Thread[] threads = new Thread[num];
        for(int i=0; i<num; i++){
            threads[i] = new CounterThread(i);
            threads[i].start();
        }

        for(int i=0; i<num; i++){
            threads[i].join();
        }

        System.out.println(counter);
        System.out.println(counter2);
        System.out.println(integer.get());
        System.out.println(switcher.isOn());
    }
}