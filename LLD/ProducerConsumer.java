package com.coding.lld;

import java.util.LinkedList;
import java.util.List;

public class ProducerConsumer {
    public static void main(String[] args) {
        PC pc = new PC();
        Thread t = new Thread(()->{
            try {
                pc.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(()->{
            try {
                pc.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }) ;
        t.start();
        t2.start();
    }
}

class PC{

    LinkedList<Integer> list = new LinkedList<>();
    private int capacity = 2;

    public synchronized void produce() throws InterruptedException {
        int value = 0;
        int i =0;
        while(i++<6) {
            while(list.size() == capacity) {
                wait();
            }
            System.out.println("Producer produced : "+value);
            list.add(value++);
            notify();
        }
    }

    public synchronized void consume() throws InterruptedException {
        int i =0;
        while(i++ <6) {
            while(list.isEmpty()) {
                wait();
            }
            int item = list.removeFirst();
            System.out.println("Consumer consumed : "+item);
            notify();
        }
    }
}
