package delayed;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class QueuedProcessor {

    protected BlockingQueue<Process> queue;
    protected List<Thread> pool;

    {
        queue = new LinkedBlockingQueue<>();
        pool = new Vector<>();
    }

    public abstract boolean offer(Process process);

    public abstract boolean offer(IJob job);

    public abstract void start(int poolSize);

    public abstract void stop();

}
