package com.baz.caja.job.runner;

import com.baz.caja.job.core.Job;

/**
 * Todo ejecutor de Jobs debe implementar esta interfaz,
 * Aqu� se define el comportamiento mediante el cual se interactua con
 * un {@link JobManager}.
 * Un JobRunner debe se administra mediante {@link JobManager}
 * @author Jos� V�zquez
 * @version 0.1
 */
public interface JobRunner {
    
    /**
     * M�todo que otorga el siguiente Job a ejecutar.
     * null significa que no hay m�s Jobs por correr.
     * Se debe enviar null para representar el fin del procesamiento de Jobs.
     * @return Siguiente Job a ejecutar o null para terminar.
     */
    abstract Job getNextJob();   
    
    /**
     * Este m�todo indica que el procesamiento debe detenerse hasta nuevo aviso.
     * Generalmente ser� usado para indicar que se ha llegado al l�mite de Jobs concurrentes.
     * La interrupci�n del procesamiento se efectua en JobManager haciendo un wait() sobre la referencia 
     * de JobRunner que este corriendo.
     * Si se decide implementar este comportamiento (regresando el valor true)
     * se debe definir tambi�n el punto de continuaci�n del procesamiento (generalmente en el m�todo jobDone)
     * haciendo un notify sobre la referencia 
     * de JobRunner que este corriendo.
     * @return true pausa la ejecuci�n de Jobs posteriores / false continua la ejecuci�n normal.
     */
    abstract boolean halt();
    
    /**
     * Este m�todo notifica que un Job ha finalizado
     * Si se requiere continuar con la operaci�n tras haber regresado un halt en true 
     * se debe hacer this.notify(); en este m�todo
     * @param b El Job que reci�n ha finalizado
     */
    public void jobDone(Job b);
    
}
