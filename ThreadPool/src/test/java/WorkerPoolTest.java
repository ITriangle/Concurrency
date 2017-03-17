import org.junit.Test;

import java.util.concurrent.*;

/**
 * Created by wangl on 2017/3/13.
 */
public class WorkerPoolTest {

    @Test
    public void test() throws InterruptedException {

        //RejectedExecutionHandler implementation
        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();

        //Get the ThreadFactory implementation to use
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        //creating the ThreadPoolExecutor
        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2), threadFactory, rejectionHandler);


        //start the monitoring thread
        MyMonitorThread monitor = new MyMonitorThread(executorPool, 3);
        Thread monitorThread = new Thread(monitor);
        monitorThread.start();

        //submit work to the thread pool
        for (int i = 0; i < 10; i++) {
            executorPool.execute(new WorkerThread("cmd" + i));


        }

        Thread.sleep(30000);

        //shut down the pool
        executorPool.shutdown();

        //shut down the monitor thread
        Thread.sleep(5000);
        monitor.shutdown();

    }

    @Test
    public void test1(){

         int COUNT_BITS = Integer.SIZE - 3;
         int CAPACITY   = (1 << COUNT_BITS) - 1;

         int RUNNING    = -1 << COUNT_BITS;
         int SHUTDOWN   =  0 << COUNT_BITS;
         int STOP       =  1 << COUNT_BITS;
         int TIDYING    =  2 << COUNT_BITS;
         int TERMINATED =  3 << COUNT_BITS;



        System.out.println("COUNT_BITS  :" + COUNT_BITS);
        System.out.println("CAPACITY    :" + Integer.toBinaryString(CAPACITY));
        System.out.println("RUNNING     :" + Integer.toBinaryString(RUNNING));
        System.out.println("SHUTDOWN    :" + Integer.toBinaryString(SHUTDOWN));
        System.out.println("STOP        :" + Integer.toBinaryString(STOP));

        System.out.println("TIDYING     :" + Integer.toBinaryString(TIDYING));
        System.out.println("TERMINATED  :" + Integer.toBinaryString(TERMINATED));
    }
}
