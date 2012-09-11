package com.baz.caja.job.runner;

import java.util.Iterator;
import java.util.List;
import com.baz.caja.job.core.Job;

/**
 * Esta implementación de JobRunner se alimenta de una lista para obtener los Jobs a ejecutar.
 * Se especifica un límite de Jobs concurrentes mediante setLimit(int) 
 * Por default este límite es de 5 Jobs.
 * 
 * 
 * @author José Vázquez
 * @version 0.1
 */
public class ListJobRunner implements JobRunner{

    private List<Job> jobs;
    private Iterator<Job> ite;
    private int alive=0;
    private int limit =5;

    /**
     * Crea este objeto con una lista de <code>Job</code> a ejecutar
     * @param jobs 
     */
    public ListJobRunner(List<Job> jobs) {
        this.jobs = jobs;
    }
    
    /**
     * Controla el flujo de ejecución.
     * Se debe especificar una lista via setJobs(List) antes de que se invoque este método.
     * De lo contrario se lanzará una excepción IllegalStateException "ListJobRunner must be feed with a list first"
     * Se hace uso un Iterator para recorrer la lista proporcionada.
     * @return regresa el siguiente Job a ejecutar
     */
    @Override synchronized public Job getNextJob() {
        if(ite==null){
            if(jobs==null) throw new IllegalStateException("ListJobRunner must be feed with a list first");
            ite = jobs.iterator();
        } 
        if(ite.hasNext()){
            goUp();
            Job j = ite.next();
            j.setRunner(this);
            return j;
        } 
        else{
            return null;
        } 
    }
    
    @Override synchronized public void jobDone(Job j) {
        goDown();
//        System.out.println("Job"+j.getName()+" Done"+ getAlive());
        this.notify();
    }

    @Override synchronized public boolean halt() {
        return getAlive() >= limit;
    }
    
    /**
     * Aumenta el número de <code>Job</code> vivos
     */
    synchronized private void goUp(){
        alive++;
    }
    
    /**
     * Decrementa el número de <code>Job</code> vivos
     */
    synchronized private void goDown(){
        alive--;
    }
    
    /**
     * Regresa el número de <code>Job</code> vivos
     * @return número de <code>Job</code> vivos
     */
    synchronized public int getAlive(){
        return alive;
    }

    
    //<editor-fold defaultstate="collapsed" desc="Getters Setters">
    public List<Job> getJobs() {
        return jobs;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    //</editor-fold>
    
}
