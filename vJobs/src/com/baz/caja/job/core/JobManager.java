package com.baz.caja.job.core;

import com.baz.caja.job.runner.JobRunner;
import com.baz.caja.job.events.JobEvent;
import com.baz.caja.job.events.JobListener;
import javax.swing.event.EventListenerList;

/**
 * El manejador de <code>Job</code> es el punto de ejecución de cualquier {@link JobRunner}
 * Este es el encardago de llevar el flujo de ejecución, siguiendo las reglas especificadas en {@link JobRunner}
 * 
 * 
 * @author José Vázquez
 * @version 0.1
 */
public class JobManager {

    final private JobRunner jobRunner;
    private EventListenerList listenerList = new EventListenerList();
    private boolean noMoreJobs=false;
    
    /**
     * Inicializa este objeto con el {@link JobRunner job} especificado.
     * @param job 
     */
    public JobManager(JobRunner job) {
        this.jobRunner=job;
    }
    
    private int jobsRunned;
    private int jobsFinished;
    
    
    /**
     * Punto de inicio.
     * Se invoca este método para dar inicio a la ejecución de todos los <code>Job</code> registrados en el {@link JobRunner}
     */
    public void runJobs(){
        final JobManager thisManager = this;
        if(jobRunner==null) throw new IllegalStateException("No existe un JobRunner asignado a este manager");
        
        int i =0;
        Job nextJob = jobRunner.getNextJob();
        while(nextJob!=null){
            
            final Job actualJob =nextJob;
            
            //Do CleanUp upon Job Finish
            nextJob.addJobHasFinishedListener(new JobListener() {
                @Override public void jobHasFinished(JobEvent evt) {
                    actualJob.cleanUp();
                    //System.out.println("JobFinished:"+actualJob.getName()+" "+(jobsRunned-jobsFinished));
                    thisManager.jobFinished(actualJob);
                }
            });
            
            if(nextJob.getName()==null) nextJob.setName("Job"+System.currentTimeMillis());
            new Thread(nextJob,nextJob.getName()).start();
            i++;jobsRunned++;
            if(jobRunner.halt()) {
                synchronized(jobRunner){
                    try {jobRunner.wait();} catch (InterruptedException ex) {}
                } 
            }
            
            nextJob = jobRunner.getNextJob();
        }
        //noMoreJobs
        noMoreJobs=true;
    }
    
    /**
     * Se invoca este método para declarar que un determinado <code>Job</code> ha finalizado.
     * Si se detecta que es el último <code>Job</code> en ejecutarse se dispara el evento fireJobManagerHasFinishedEvent
     * @param j <code>Job</code> que recién finalizó
     */
    synchronized private void jobFinished(Job j){
        jobsFinished++;
        if(noMoreJobs && jobsFinished==jobsRunned){
            System.out.println(jobsFinished+" "+jobsRunned);
            jobsFinished=0;jobsRunned=0;
            fireJobManagerHasFinishedEvent(new JobEvent(j));
        } 
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Código para manejar evento de terminado">
    public void addJobManagerHasFinishedListener(JobListener listener) {
        listenerList.add(JobListener.class, listener);
    }
    // This methods allows classes to unregister for MyEvents
    
    public void removeJobManagerHasFinishedListener(JobListener listener) {
        listenerList.remove(JobListener.class, listener);
    }
    
    private void fireJobManagerHasFinishedEvent(JobEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == JobListener.class) {
                ((JobListener) listeners[i + 1]).jobHasFinished(evt);
            }
        }
    }
    //</editor-fold>
    
}
