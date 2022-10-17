package com.coding.lld;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    public static void main(String[] args) throws InterruptedException {

        SlidingWindowLog rateLimiter = new SlidingWindowLog();
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(() -> {

                try {
                    if (rateLimiter.isLimitReached("user1")) {
                        System.out.println("user1: sorry too many request");

                    } else {
                        System.out.println("user1: continue work ....");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            t.start();

            Thread t2 = new Thread(() -> {

                try {
                    if (rateLimiter.isLimitReached("user2")) {
                        System.out.println("user2: sorry too many request");

                    } else {
                        System.out.println("user2: continue work ....");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            t2.start();
            Thread.sleep(10000);
        }

    }
}


class SlidingWindowLog {
    private int maxLimit = 4;
    private ConcurrentHashMap<String, Object> userIdObject = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Queue<Long>> userWindowMap = new ConcurrentHashMap<>();

    public boolean isLimitReached(String userId) throws InterruptedException {

        userIdObject.putIfAbsent(userId, new Object());
        Thread.sleep(1000);
        synchronized(userIdObject.get(userId)) {
            long curSec = System.currentTimeMillis()/1000;
            long minSec = curSec - 60;
            Queue<Long> log = userWindowMap.get(userId);
            if(log==null || log.isEmpty()){
                userWindowMap.put(userId, new LinkedList<Long>(Arrays.asList(curSec)));
                return false;
            } else {
                log = userWindowMap.get(userId);
                System.out.println(log);
                while(log.peek() < minSec){
                    log.poll();
                }

                log.add(curSec);

                if(log.size() <= 4)
                    return false;
                else return true;
            }

        }
    }

}


class WindowDetails {
    private long time;
    private int count;

    WindowDetails(long time, int count) {
        this.time = time;
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }

    @Override
    public String toString() {
        return "WindowDetails{" + "time=" + time + ", count=" + count + '}';
    }
}

