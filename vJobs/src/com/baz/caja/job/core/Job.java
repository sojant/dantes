package com.baz.caja.job.core;

import com.baz.caja.job.runner.JobRunner;
import com.baz.caja.job.events.JobEvent;
import com.baz.caja.job.events.JobListener;
import javax.swing.event.EventListenerList;

/**
 * Esta clase representa una unidad de trabajo.
 * Debe ser considerada como la ejecución específica de un hilo.
 * Se especifica el comportamiento de un <code>Job</code> implementando el método <code>execute()</code>
 * Un <code>Job</code> debe se ejecutado mediante un {@link JobRunner}
 * @author José Vázquez
 * @version 0.1
 * @see JobRunner
 */
public abstract class Job implements Runnable{

    /**
     * Nombre del <code>Job</code>
     */
    private String name;
    
    private JobRunner runner;
    private EventListenerList listenerList = new EventListenerList();

    /**
     * Este método se implementa para definir el comportamiento del <code>Job</code>
     */
    public abstract void execute();
    
    /**
     * Define tareas de limpieza al terminar el trabajo actual.
     * Este método se invoca desde el {@link JobManager} una vez finalizado el <code>Job</code>
     */
    public abstract void cleanUp();
    
    final Job actual=this;
    
    /**
     * Punto de entrada de un <code>Job</code>, registra el evento de finalización, inicia el hilo y dispara el evento de
     * finalización a todos los escuchas.
     */
    final @Override public void run() {
        
        //Antes de iniciar registra el evento que anunciará que el Job ha terminado
        addJobHasFinishedListener(new JobListener() {
            @Override public void jobHasFinished(JobEvent evt) {
                if(runner!=null)runner.jobDone(actual);
            }
        });
        
        //Ejecuta el Job
        execute();
        
        //Dispara evento JobHasFinished
        fireJobHasFinishedEvent(new JobEvent(this));
    }

    
    //<editor-fold defaultstate="collapsed" desc="Código para manejar evento de terminado">
    public void addJobHasFinishedListener(JobListener listener) {
        listenerList.add(JobListener.class, listener);
    }
    // This methods allows classes to unregister for MyEvents
    
    public void removeJobHasFinishedListener(JobListener listener) {
        listenerList.remove(JobListener.class, listener);
    }
    
    private void fireJobHasFinishedEvent(JobEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == JobListener.class) {
                ((JobListener) listeners[i + 1]).jobHasFinished(evt);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters y Setters">
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public JobRunner getRunner() {
        return runner;
    }
    
    public void setRunner(JobRunner runner) {
        this.runner = runner;
    }
    //</editor-fold>
    
}
