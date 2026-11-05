package pedidos;

import java.util.HashMap;
import java.util.Map;

/**
 * RF08 e RF09: Catálogo de modelos.
 * criar() sempre devolve cópia, nunca o original.
 */
public class CatalogoDePedidos {
    private final Map<String, Pedido> modelos = new HashMap<>();

    public void registrar(String chave, Pedido modelo) {
        modelos.put(chave, modelo);
    }

    /**
     * RF08: Devolve cópia profunda.
     * RF09: Lança IllegalArgumentException se chave não existir.
     */
    public Pedido criar(String chave) {
        Pedido modelo = modelos.get(chave);
        if (modelo == null) {
            throw new IllegalArgumentException("Chave não registrada: " + chave);
        }
        return modelo.clonar();
    }
}