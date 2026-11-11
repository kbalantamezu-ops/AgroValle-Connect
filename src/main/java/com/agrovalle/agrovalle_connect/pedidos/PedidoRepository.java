package com.agrovalle.agrovalle_connect.pedidos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    String CON_ITEMS = """
            select distinct p from Pedido p
            left join fetch p.items i
            left join fetch i.producto prod
            left join fetch prod.agricultor
            left join fetch p.pago pag
            left join fetch pag.medioPago
            """;

    @Query(CON_ITEMS + " order by p.fechaCreacion desc")
    List<Pedido> buscarTodosConItems();

    @Query(CON_ITEMS + " where p.usuarioId = :usuarioId order by p.fechaCreacion desc")
    List<Pedido> buscarPorUsuarioConItems(@Param("usuarioId") Long usuarioId);

    @Query(CON_ITEMS + """
             where exists (
                 select 1 from ItemPedido item
                 where item.pedido = p and item.producto.agricultor.id = :agricultorId)
             order by p.fechaCreacion desc
            """)
    List<Pedido> buscarPorAgricultorConItems(@Param("agricultorId") Long agricultorId);

    @Query(CON_ITEMS + " where p.id = :id")
    Optional<Pedido> buscarPorIdConItems(@Param("id") Long id);
}
