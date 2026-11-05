package pedidos;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * RF02 e RF03: Representa uma linha de pedido.
 * Agora implementa Serializable para suportar clonarPorSerializacao() do Pedido.
 */
public class ItemPedido implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String produto;
    private int quantidade;
    private final BigDecimal precoUnitario;

    public ItemPedido(String produto, int quantidade, BigDecimal precoUnitario) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    /**
     * RF03: Construtor de cópia independente.
     */
    public ItemPedido(ItemPedido origem) {
        this.produto = origem.produto;
        this.quantidade = origem.quantidade;
        this.precoUnitario = origem.precoUnitario;
    }

    /**
     * RF02: Subtotal = quantidade × preço unitário.
     */
    public BigDecimal subtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public String getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
}