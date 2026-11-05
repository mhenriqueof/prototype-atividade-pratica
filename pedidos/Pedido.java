package pedidos;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * RF04, RF05, RF06, RF07 e RF12.
 * Implementa Prototype<Pedido> com cópia profunda manual e por serialização.
 */
public class Pedido implements Prototype<Pedido>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // RF05: ID sequencial único iniciando em 1
    private static final AtomicLong SEQUENCIAL = new AtomicLong(1);

    private final long id;
    private String cliente;
    private String enderecoEntrega;
    private final List<ItemPedido> itens;
    private final LocalDate criadoEm;

    public Pedido(String cliente, String enderecoEntrega, List<ItemPedido> itens) {
        this.id = SEQUENCIAL.getAndIncrement();
        this.cliente = cliente;
        this.enderecoEntrega = enderecoEntrega;
        // Cópia defensiva no construtor
        this.itens = new ArrayList<>(itens);
        this.criadoEm = LocalDate.now();
    }

    /**
     * RF04: Soma dos subtotais com duas casas decimais.
     */
    public BigDecimal valorTotal() {
        return itens.stream()
                .map(ItemPedido::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * RF06 e RF07: Cópia profunda manual.
     * Novo ID e nova data de criação. Lista e itens são novos objetos.
     */
    @Override
    public Pedido clonar() {
        List<ItemPedido> itensCopiados = new ArrayList<>();
        for (ItemPedido item : this.itens) {
            itensCopiados.add(new ItemPedido(item)); // Usa construtor de cópia
        }
        return new Pedido(this.cliente, this.enderecoEntrega, itensCopiados);
    }

    /**
     * RF12 (Desafio): Cópia profunda via serialização.
     * Respeita RF06 e RF07 porque o construtor é chamado na desserialização
     * apenas se usarmos readResolve, mas aqui criamos novo objeto explicitamente.
     */
    public Pedido clonarPorSerializacao() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            Pedido originalDesserializado = (Pedido) ois.readObject();

            // RF07: Novo ID e nova data (construtor gera ambos)
            // Precisamos reconstruir pois a desserialização não chama o construtor normal
            List<ItemPedido> itensCopiados = new ArrayList<>();
            for (ItemPedido item : originalDesserializado.itens) {
                itensCopiados.add(new ItemPedido(item));
            }
            return new Pedido(originalDesserializado.cliente, 
                              originalDesserializado.enderecoEntrega, 
                              itensCopiados);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Falha na clonagem por serialização", e);
        }
    }

    // Métodos para ajuste nas cópias (RF10 cenário de teste)
    public void setCliente(String cliente) { this.cliente = cliente; }
    public void setEnderecoEntrega(String endereco) { this.enderecoEntrega = endereco; }
    public List<ItemPedido> getItens() { return itens; } // Referência direta para manipulação no teste
    public long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public LocalDate getCriadoEm() { return criadoEm; }
}