import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by wangl on 2017/3/13.
 */
public class ExecutorSubmitTest {

    @Test
    public void test() {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        Future<String> future = executor.submit(new TaskImp());

        try {
            String result = future.get();

            System.out.println(result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        System.out.println("Finished all threads");
    }

}
