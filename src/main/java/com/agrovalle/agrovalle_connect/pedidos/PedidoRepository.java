package com.agrovalle.agrovalle_connect.pedidos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
            select distinct p from Pedido p
            left join fetch p.items i
            left join fetch i.producto prod
            left join fetch prod.agricultor
            left join fetch p.pago pag
            left join fetch pag.medioPago
            order by p.fechaCreacion desc
            """)
    List<Pedido> buscarTodosConItems();

    @Query("""
            select distinct p from Pedido p
            left join fetch p.items i
            left join fetch i.producto prod
            left join fetch prod.agricultor
            left join fetch p.pago pag
            left join fetch pag.medioPago
            where p.id = :id
            """)
    Optional<Pedido> buscarPorIdConItems(@Param("id") Long id);
}
