package recorrido_de_arboles;

import java.util.ArrayList;

/**
 *
 * @author Alfredo Emmanuel Garcia Falcon
 * @param <T>
 */
public class nodo<T> {
    public int colum;
    public int line;
    public T dato;
    public nodo padre;
    public ArrayList<nodo> hijos = new ArrayList();
    
    public nodo(T valor, int colum, int line)
    {
        this.dato = valor;
        this.colum = colum;
        this.line = line;
        this.padre = null;
    }
}
