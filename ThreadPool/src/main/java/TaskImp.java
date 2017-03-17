import java.util.concurrent.Callable;

/**
 * Created by wangl on 2017/3/13.
 */
public class TaskImp implements Callable<String> {
    @Override
    public String call() throws Exception {

        Thread.sleep(2000);

        return "This is future task";
    }
}
