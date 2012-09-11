/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vruns;

import com.baz.caja.job.pool.ObjectPool;
import com.baz.caja.job.core.JobManager;
import com.baz.caja.job.core.Job;
import com.baz.caja.job.runner.ListJobRunner;
import com.baz.caja.job.events.JobEvent;
import com.baz.caja.job.events.JobListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author José Vázquez
 * @version 0.1
 */
public class VRuns {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        
        //Setup Configuration
        int totalJobs = 50000;
        int currentJobsLimit = 25;
        int totalPoolObjs = 25;
        final int timeDelay = 100;
        
        //Feed ObjectPool Reusable Thread Specific Objects (Usually Conns or PS's )
        final ObjectPool<String> pool = new ObjectPool<String>();
        for (int i = 0; i < totalPoolObjs; i++) {pool.put("obj"+i);}

        //Create Jobs
        List<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i < totalJobs; i++) {
            final int ii =i;
            
            class Job1 extends Job{
                String sPool;
                
                @Override public void execute() {
                    
                    try {
                        sPool=pool.pop();
                    } catch (Exception e) {
                        //No se pudo obtener recursod e Pool
                        setName(String.valueOf(ii+" Error "+e));
                        return;
                    }
                    setName(String.valueOf(ii+" "+sPool));
                    try {Thread.sleep(new Random().nextInt(timeDelay));} catch (InterruptedException ex) {}
                    
                }
            
                //Do some cleanUp after Job Finish
                @Override public void cleanUp() {
                    //Regresa objeto a Pool y notifica si alguien mas en su espera
                    pool.putNotify(this.sPool);
                }
                
            }
            
            Job j = new Job1();
            jobs.add(j);
        }
        
        for (Job job : jobs) {job.setName("");}
        
        //Create JobRunner
        ListJobRunner jr = new ListJobRunner(jobs);
        
        //Sepcify concurrent Jobs
        jr.setLimit(currentJobsLimit);
        
        //Assign to JobManager
        JobManager mgr = new JobManager(jr);
        mgr.addJobManagerHasFinishedListener(new JobListener() {
            @Override public void jobHasFinished(JobEvent evt) { System.out.println("All Jobs Finished"); }
        });
        
        mgr.runJobs();
        
    }
    
    public void main1() throws InterruptedException{
        final Date d = new Date();
        
        synchronized(d){
            d.notify();
        }
        
        new Thread(new Runnable() {
            @Override public void run() {
                try {Thread.sleep(5000);} catch (InterruptedException ex) {}
                synchronized(d){
                    d.notify();
                }
                
            }
        }).start();
        
        synchronized(d){
            d.wait();
        }
        
        System.out.println("Finish");
    }
}
