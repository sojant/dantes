package com.baz.caja.job.pool;

import java.util.LinkedList;

/**
 * Esta clase provee un mecanismo de acceso a objetos seguro en un ambiente multihilo.
 * @author José Vázquez
 * @version 0.1
 */
public class ObjectPool<E> {
    
    /**
     * Se hace uso de un {@link LinkedList} como backbone para este pool.
     */
    final private LinkedList<E>q = new LinkedList<E>();
    
    /**
     * Obtiene un objeto del pool que este disponible.
     * Si no hay objetos libres, espera a que este disponible el siguiente.
     * En teoría este método debe esperar de 0 a 1 vez por un objeto libre.
     * En un caso muy extraño (aún no imposible) que espere 10 veces por un objeto, algo esta mal
     * y se arrojará la correspondiente excepción IllegalStateException "Waited 10 times to pop from ObjectPool. Something is wrong..."
     * @return regresa un objeto garantizado de ser único por hilo.
     */
    public synchronized E pop(){
        
        int waits=0;
        while(q.isEmpty()){
            //System.out.println("Wait"+waits);
            //Espera en la notificación que haya elementos en el Pool
            try {waits++;this.wait();} catch (InterruptedException ex){}
            if(waits==10) throw new IllegalStateException("Waited 10 times to pop from ObjectPool. Something is wrong...");
        }
        
        //Con la validación de los 10 Waits en teoría nunca debe arrojarse esta excepción.
        if(q.isEmpty()) throw new IllegalStateException("ObjectPool esta vacío");
        return q.pop();
    }
    
    /**
     * Regresa un objeto al Pool y notifica a cualquier hilo en espera
     * @param e Objeto para notificar en el Pool
     */
    synchronized public void putNotify(E e){
        q.push(e);this.notify();
    }
    
    /**
     * Agrega un Objeto al Pool sin hacer la notificación correspondiente. Se usa este método para inicializar el Pool
     * antes de que empieze a ser accedido
     * @param e Objeto para agregar al Pool
     */
    public void put(E e){
        q.push(e);
    }
    

    /**
     * Obtiene la lista que funge como Backbone para este Pool
     * @return regresa la lista de objetos
     */
    public LinkedList<E> getList(){
        return q;
    }
    
}
