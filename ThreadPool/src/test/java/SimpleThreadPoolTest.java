import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangl on 2017/3/13.
 */
public class SimpleThreadPoolTest {

    @Test
    public void test() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {

            Runnable worker = new WorkerThread("" + i);
            executor.execute(worker);

        }
        executor.shutdown();

        while (!executor.isTerminated()) {

        }

        System.out.println("Finished all threads");
    }
}
