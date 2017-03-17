package Reentrant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangl on 2017/3/17.
 */
public class ReentrantWriteLock {
    private Map<Thread, Integer> readingThreads  = new HashMap<Thread, Integer>();

    private int writeAccesses = 0;
    private int writeRequests = 0;
    private Thread writingTread = null;

    public synchronized void lockWrite() throws InterruptedException {

        writeRequests++;
        Thread callingThread = Thread.currentThread();
        while(!canGrantWriteAccess(callingThread)){
            wait();
        }

        writeRequests--;
        writeAccesses++;
        writingTread = callingThread;
    }

    private boolean canGrantWriteAccess(Thread callingThread) {

        if (hasReader()) return false;

        if (writingTread == null) return true;

        if(!isWriter(callingThread)) return false;

        return true;
    }

    private boolean isWriter(Thread callingThread) {

        return writingTread == callingThread;
    }

    private boolean hasReader() {

        return readingThreads.size() > 0;
    }

    public synchronized void unlockWrite(){
        writeAccesses--;

        if (writeAccesses == 0){

            writingTread = null;
        }


        notifyAll();
    }

}
