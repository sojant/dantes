package com.baz.caja.job.events;

import java.util.EventListener;
/**
 * Listener que representa la finalizaci�n de un Job
 * @author Jos� V�zquez
 * @version 0.1
 */
public interface JobListener extends EventListener{
    public void jobHasFinished(JobEvent evt);
    
}
