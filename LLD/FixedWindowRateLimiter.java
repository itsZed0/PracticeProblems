package com.coding.lld;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimiter {
    public static void main(String[] args) throws InterruptedException {

        FixedWindow rateLimiter = new FixedWindow();
        for(int i=0; i < 20; i++ ) {
            Thread t = new Thread(()->{

                try {
                    if(rateLimiter.isLimitReached("user1")) {
                        System.out.println("user1: sorry too many request");

                    } else {
                        System.out.println("user1: continue work ....");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            t.start();

            Thread t2 = new Thread(()->{

                try {
                    if(rateLimiter.isLimitReached("user2")) {
                        System.out.println("user2: sorry too many request");

                    } else {
                        System.out.println("user2: continue work ....");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
            t2.start();
            Thread.sleep(6000);
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
        return "WindowDetails{" +
                "time=" + time +
                ", count=" + count +
                '}';
    }
}

class FixedWindow {
    ConcurrentHashMap<String, WindowDetails> userWindowDetails = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Object> userIdObject = new ConcurrentHashMap<>();
    private int maxLimit = 4;

   public boolean  isLimitReached(String userId) throws InterruptedException {
        System.out.println("user :" + userId);
        Thread.sleep(1000);
       userIdObject.putIfAbsent(userId, new Object());

       synchronized (userIdObject.get(userId)) {

            System.out.println("user inside :" + userId);
            long curSec = System.currentTimeMillis()/1000;
            WindowDetails wD = userWindowDetails.get(userId);
            if(wD == null ){
                userWindowDetails.put(userId, new WindowDetails(curSec,1));
                return false;
            } else {
                long diff = curSec - wD.getTime();

                if(diff > 60) {
                    userWindowDetails.put(userId, new WindowDetails(curSec, 1));
                    return false;
                } else {
                    if (wD.getCount() + 1 > maxLimit) {
                        return true;
                    } else {
                        wD.incrementCount();
                        return false;
                    }
                }
            }

        }
   }

}
