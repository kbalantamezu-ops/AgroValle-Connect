package com.agrovalle.agrovalle_connect.productos;

<<<<<<< HEAD
import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("""
            select p from Producto p
            join fetch p.agricultor
            where (:q is null or lower(p.nombre) like lower(concat('%', cast(:q as string), '%')))
              and (:municipio is null or p.agricultor.municipio = :municipio)
              and (:categoria is null or p.categoria = :categoria)
              and (:disponible is null or p.disponible = :disponible)
            order by p.fechaPublicacion desc
            """)
    List<Producto> buscar(@Param("q") String q, @Param("municipio") Municipio municipio,
            @Param("categoria") Categoria categoria, @Param("disponible") Boolean disponible);

    @Query("select p from Producto p join fetch p.agricultor where p.id = :id")
    Optional<Producto> buscarPorId(@Param("id") Long id);

    List<Producto> findByCategoria(Categoria categoria);
=======
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
>>>>>>> origin/develop
}
