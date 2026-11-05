package pedidos;

/**
 * RF01: Interface genérica Prototype<T>.
 * RT04: Não usa Cloneable nem Object.clone().
 */
public interface Prototype<T> {
    T clonar();
}