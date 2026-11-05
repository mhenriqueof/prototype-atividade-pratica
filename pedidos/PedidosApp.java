package pedidos;

import java.math.BigDecimal;
import java.util.List;

/**
 * RF10, RF11 e RF12.
 * RT05: Main sem regras de negócio, apenas teste e verificação.
 */
public class PedidosApp {
    public static void main(String[] args) {
        // === Cenário de Teste (RF10) ===
        CatalogoDePedidos catalogo = new CatalogoDePedidos();

        // Modelo base
        List<ItemPedido> itensModelo = List.of(
                new ItemPedido("Café 500g", 10, new BigDecimal("18.90")),
                new ItemPedido("Açúcar 1kg", 20, new BigDecimal("4.50")),
                new ItemPedido("Filtro de papel", 5, new BigDecimal("7.20"))
        );
        Pedido modelo = new Pedido("(modelo)", "-", itensModelo);
        catalogo.registrar("reposicao-mensal", modelo);

        // Pedido A: nenhum ajuste
        Pedido pedidoA = catalogo.criar("reposicao-mensal");
        pedidoA.setCliente("Mercado Central");
        pedidoA.setEnderecoEntrega("Rua das Flores, 120");

        // Pedido B: alterar qtd Café para 20
        Pedido pedidoB = catalogo.criar("reposicao-mensal");
        pedidoB.setCliente("Padaria Boa Massa");
        pedidoB.setEnderecoEntrega("Av. Paraná, 800");
        pedidoB.getItens().stream()
                .filter(i -> i.getProduto().equals("Café 500g"))
                .findFirst()
                .ifPresent(i -> i.setQuantidade(20));

        // Pedido C: adicionar Adoçante
        Pedido pedidoC = catalogo.criar("reposicao-mensal");
        pedidoC.setCliente("Restaurante do Porto");
        pedidoC.setEnderecoEntrega("Rua do Cais, 45");
        pedidoC.getItens().add(new ItemPedido("Adoçante 100ml", 12, new BigDecimal("6.00")));

        // Impressão dos pedidos
        imprimirPedido(modelo);
        imprimirPedido(pedidoA);
        imprimirPedido(pedidoB);
        imprimirPedido(pedidoC);

        System.out.println("\n=== Critérios de Aceitação (RF11) ===");
        verificarCA01(modelo, pedidoA, pedidoB, pedidoC);
        verificarCA02(modelo);
        verificarCA03(pedidoA, pedidoB, pedidoC);
        verificarCA04(modelo);
        verificarCA05(modelo, pedidoA, pedidoB, pedidoC);
        verificarCA06(catalogo);
        verificarCA07(modelo);

        // === Desafio RF12: Benchmark ===
        System.out.println("\n=== Benchmark RF12 ===");
        benchmarkClonagem(modelo);
    }

    private static void imprimirPedido(Pedido p) {
        System.out.printf("Pedido#%d | %s | %s | itens=%d | total=R$ %s%n",
                p.getId(), p.getCliente(), p.getEnderecoEntrega(),
                p.getItens().size(), p.valorTotal());
    }

    // === Verificações CA-01 a CA-07 ===
    private static void verificarCA01(Pedido m, Pedido a, Pedido b, Pedido c) {
        boolean ok = m.getId() != a.getId() && m.getId() != b.getId() 
                  && m.getId() != c.getId() && a.getId() != b.getId() 
                  && a.getId() != c.getId() && b.getId() != c.getId();
        System.out.println("CA-01 IDs diferentes: " + (ok ? "OK" : "FALHA"));
    }

    private static void verificarCA02(Pedido modelo) {
        boolean ok = modelo.valorTotal().compareTo(new BigDecimal("315.00")) == 0;
        System.out.println("CA-02 Modelo mantém R$ 315,00: " + (ok ? "OK" : "FALHA"));
    }

    private static void verificarCA03(Pedido a, Pedido b, Pedido c) {
        boolean okA = a.valorTotal().compareTo(new BigDecimal("315.00")) == 0;
        boolean okB = b.valorTotal().compareTo(new BigDecimal("504.00")) == 0;
        boolean okC = c.valorTotal().compareTo(new BigDecimal("387.00")) == 0;
        System.out.println("CA-03 Totais corretos (A/B/C): " + (okA && okB && okC ? "OK" : "FALHA"));
    }

    private static void verificarCA04(Pedido modelo) {
        boolean ok = modelo.getItens().size() == 3;
        System.out.println("CA-04 Modelo continua com 3 itens: " + (ok ? "OK" : "FALHA"));
    }

    private static void verificarCA05(Pedido m, Pedido a, Pedido b, Pedido c) {
        boolean ok = true;
        for (ItemPedido im : m.getItens()) {
            for (ItemPedido ia : a.getItens()) if (im == ia) ok = false;
            for (ItemPedido ib : b.getItens()) if (im == ib) ok = false;
            for (ItemPedido ic : c.getItens()) if (im == ic) ok = false;
        }
        System.out.println("CA-05 Nenhuma instância compartilhada: " + (ok ? "OK" : "FALHA"));
    }

    private static void verificarCA06(CatalogoDePedidos catalogo) {
        boolean ok = false;
        try {
            catalogo.criar("inexistente");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        System.out.println("CA-06 Exceção para chave inexistente: " + (ok ? "OK" : "FALHA"));
    }

    private static void verificarCA07(Pedido modelo) {
        boolean ok = "(modelo)".equals(modelo.getCliente());
        System.out.println("CA-07 Cliente do modelo preservado: " + (ok ? "OK" : "FALHA"));
    }

    // === RF12: Benchmark ===
    private static void benchmarkClonagem(Pedido modelo) {
        int iteracoes = 100_000;

        long inicioManual = System.currentTimeMillis();
        for (int i = 0; i < iteracoes; i++) {
            modelo.clonar();
        }
        long tempoManual = System.currentTimeMillis() - inicioManual;

        long inicioSerial = System.currentTimeMillis();
        for (int i = 0; i < iteracoes; i++) {
            modelo.clonarPorSerializacao();
        }
        long tempoSerial = System.currentTimeMillis() - inicioSerial;

        System.out.printf("clonar() manual: %d ms para %,d cópias%n", tempoManual, iteracoes);
        System.out.printf("clonarPorSerializacao(): %d ms para %,d cópias%n", tempoSerial, iteracoes);
    }
}