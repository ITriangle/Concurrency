package Reentrant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangl on 2017/3/17.
 */
public class ReentrantReadLock {

    private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();

    private int writers = 0;
    private int writeRequests = 0;


    public synchronized void lockRead() throws InterruptedException {

        Thread callingThread = Thread.currentThread();

        while (!canCrantReadAccess(callingThread)) {
            wait();
        }

        readingThreads.put(callingThread, getAccessCount(callingThread) + 1);

    }


    private boolean canCrantReadAccess(Thread callingThread) {

        if (writers > 0) return false;

        if (isReader(callingThread)) return true;

        if (writeRequests > 0) return false;

        return true;

    }

    private boolean isReader(Thread callingThread) {
        return readingThreads.get(callingThread) != null;
    }




    private int getAccessCount(Thread callingThread) {

        Integer accessCount = readingThreads.get(callingThread);
        if (accessCount == null) return 0;

        return accessCount.intValue();
    }


    public synchronized void unlockRead(){

        Thread callingThread = Thread.currentThread();

        int accessCount = getAccessCount(callingThread);

        if (accessCount == 1){
            readingThreads.remove(callingThread);
        }else{
            readingThreads.put(callingThread, (accessCount -1));
        }

    }
}
