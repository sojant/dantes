package com.baz.caja.job.runner;

import com.baz.caja.job.core.Job;

/**
 * Todo ejecutor de Jobs debe implementar esta interfaz,
 * Aquí se define el comportamiento mediante el cual se interactua con
 * un {@link JobManager}.
 * Un JobRunner debe se administra mediante {@link JobManager}
 * @author José Vázquez
 * @version 0.1
 */
public interface JobRunner {
    
    /**
     * Método que otorga el siguiente Job a ejecutar.
     * null significa que no hay más Jobs por correr.
     * Se debe enviar null para representar el fin del procesamiento de Jobs.
     * @return Siguiente Job a ejecutar o null para terminar.
     */
    abstract Job getNextJob();   
    
    /**
     * Este método indica que el procesamiento debe detenerse hasta nuevo aviso.
     * Generalmente será usado para indicar que se ha llegado al límite de Jobs concurrentes.
     * La interrupción del procesamiento se efectua en JobManager haciendo un wait() sobre la referencia 
     * de JobRunner que este corriendo.
     * Si se decide implementar este comportamiento (regresando el valor true)
     * se debe definir también el punto de continuación del procesamiento (generalmente en el método jobDone)
     * haciendo un notify sobre la referencia 
     * de JobRunner que este corriendo.
     * @return true pausa la ejecución de Jobs posteriores / false continua la ejecución normal.
     */
    abstract boolean halt();
    
    /**
     * Este método notifica que un Job ha finalizado
     * Si se requiere continuar con la operación tras haber regresado un halt en true 
     * se debe hacer this.notify(); en este método
     * @param b El Job que recién ha finalizado
     */
    public void jobDone(Job b);
    
}
