package com.baz.caja.job.events;

import java.util.EventListener;
/**
 * Listener que representa la finalización de un Job
 * @author José Vázquez
 * @version 0.1
 */
public interface JobListener extends EventListener{
    public void jobHasFinished(JobEvent evt);
    
}
