package com.coding.lld;

import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    public static void main(String[] args) throws InterruptedException {

        SlidingWindow rateLimiter = new SlidingWindow();
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
            Thread.sleep(6000);
        }
        
    }
}


class SlidingWindow {
    private int maxLimit = 4;
    private int windowLimitInSec = 60;
    private ConcurrentHashMap<String, WindowDetailsPair> windowDetailsMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> userIdObject = new ConcurrentHashMap<>();

    // #2: Better formula than #1
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
    // #1: Improved the formula above in #2
    public boolean isLimitReached(String userId) {
        userIdObject.putIfAbsent(userId, new Object());
        synchronized (userIdObject.get(userId)) {
            long curSec = System.currentTimeMillis() / 1000;
            WindowDetailsPair wD = windowDetailsMap.get(userId);
            System.out.println(wD);
            if (wD == null) {
                WindowDetailsPair wDP = new WindowDetailsPair();
                wDP.setCur(new WindowDetails(curSec, 1));
                windowDetailsMap.put(userId, wDP);
                return false;
            } else {

                long prevTime = wD.getCur().getTime();
                long diff = curSec - prevTime; //81-20 = 61
                System.out.println(diff);
                if (diff > 60) {

                    //20....80.81...140
                    int prevCount = wD.getCur().getCount();

                    long curTimeUsed = diff - windowLimitInSec; //61-60 = 1
                    long prevWindowTimeUsed = (windowLimitInSec - curTimeUsed) / windowLimitInSec; //(60-1)/60 = .98
                    long slideWindowCount = prevCount * (prevWindowTimeUsed) + 1;

                    WindowDetails prev = wD.getCur();
                    wD.setPrev(prev);
                    wD.setCur(new WindowDetails(curSec, 1));

                    windowDetailsMap.put(userId, wD);

                    if (slideWindowCount > maxLimit) {
                        return true;
                    } else {
                        return false;
                    }

                } else {
                    wD.getCur().incrementCount();
                    if (wD.getCur().getCount() > maxLimit) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }
}

class WindowDetailsPair {
    private WindowDetails prev;
    private WindowDetails cur;

    public WindowDetails getPrev() {
        return prev;
    }

    public void setPrev(WindowDetails prev) {
        this.prev = prev;
    }

    public WindowDetails getCur() {
        return cur;
    }
    
    public void setTime(long time) {
       this.time = time;
    }

    public void setCur(WindowDetails cur) {
        this.cur = cur;
    }

    @Override
    public String toString() {
        return "WindowDetailsPair{" + "prev=" + prev + ", cur=" + cur + '}';
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

