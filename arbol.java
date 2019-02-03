package recorrido_de_arboles;

/**
 *
 * @author Alfredo Emmanuel Garcia Falcon
 * @param <T>
 */
public class arbol<T> {
    nodo<T> raiz;
    
    public arbol()
    {
        this.raiz = null;
    }
    
    public nodo<T> addRoot( T valor, int colum, int line)
    {
        nodo<T> nuevo = new nodo(valor, colum, line);
        nuevo.colum = colum;
        nuevo.line = line;
        this.raiz = nuevo;
        return nuevo;
    }
    
    public nodo<T> addChild(nodo<T> padre, T valor, int colum, int line)
    {
        nodo<T> hijo = new nodo(valor, colum, line);
        hijo.padre = padre;
        padre.hijos.add(hijo);
        hijo.colum = colum;
        hijo.line = line;
        return hijo;
    }
}
