package com.baz.caja.job.events;

import java.util.EventObject;
import com.baz.caja.job.core.Job;
/**
 * Evento que representa un cambio en el ciclo de vida de un Job
 * @author José Vázquez
 * @version 0.1
 */
public class JobEvent extends EventObject{

    public JobEvent(Job source) {
        super(source);
    }
    
}
