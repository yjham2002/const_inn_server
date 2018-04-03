package delayed;

import utils.Log;

public class QueuedProcessorImpl extends QueuedProcessor {

    @Override
    public boolean offer(Process process) {
        try {
            queue.put(process);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean offer(IJob job) {
        try {
            queue.put(new Process(job));
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void start(int poolSize) {
        Log.e(this.getClass().getSimpleName(), "Started to consume.");
        for(int e = 0; e < poolSize; e++){
            Thread thread = new Thread(()->{
                while(true){
                    try{
                        Process process = queue.take();
                        process.iJob.execute();
                    }catch (Exception ex){
                        Log.e(this.getClass().getSimpleName(), "Exception threw.");
                        ex.printStackTrace();
                    }
                }
            });

            pool.add(thread);
            thread.start();
        }
    }

    @Override
    public void stop() {
        for(Thread thread : pool){
            try{
                thread.interrupt();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
