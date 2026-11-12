package com.agrovalle.agrovalle_connect.config;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorRepository;
import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import com.agrovalle.agrovalle_connect.pagos.MedioPago;
import com.agrovalle.agrovalle_connect.pagos.MedioPagoRepository;
import com.agrovalle.agrovalle_connect.pagos.Pago;
import com.agrovalle.agrovalle_connect.pedidos.EstadoPedido;
import com.agrovalle.agrovalle_connect.pedidos.ItemPedido;
import com.agrovalle.agrovalle_connect.pedidos.Notificacion;
import com.agrovalle.agrovalle_connect.pedidos.NotificacionRepository;
import com.agrovalle.agrovalle_connect.pedidos.Pedido;
import com.agrovalle.agrovalle_connect.pedidos.PedidoRepository;
import com.agrovalle.agrovalle_connect.productos.Producto;
import com.agrovalle.agrovalle_connect.productos.ProductoRepository;
import com.agrovalle.agrovalle_connect.usuarios.Rol;
import com.agrovalle.agrovalle_connect.usuarios.Usuario;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!test")
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final AgricultorRepository agricultorRepository;

    private final ProductoRepository productoRepository;

    private final PedidoRepository pedidoRepository;

    private final NotificacionRepository notificacionRepository;

    private final MedioPagoRepository medioPagoRepository;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AgricultorRepository agricultorRepository, ProductoRepository productoRepository,
            PedidoRepository pedidoRepository, NotificacionRepository notificacionRepository,
            MedioPagoRepository medioPagoRepository, UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.agricultorRepository = agricultorRepository;
        this.productoRepository = productoRepository;
        this.pedidoRepository = pedidoRepository;
        this.notificacionRepository = notificacionRepository;
        this.medioPagoRepository = medioPagoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (medioPagoRepository.count() == 0) {
            medioPagoRepository.save(new MedioPago("EFECTIVO"));
            medioPagoRepository.save(new MedioPago("TRANSFERENCIA"));
            medioPagoRepository.save(new MedioPago("TARJETA"));
        }
        if (agricultorRepository.count() > 0) {
            sembrarUsuariosDemo();
            return;
        }
        Agricultor maria = agricultorRepository.save(new Agricultor("María Fernanda Ospina", "1111111111",
                Municipio.DAGUA, "312 555 0101", "maria.ospina@fincaelporvenir.co", "Finca El Porvenir"));
        Agricultor carlos = agricultorRepository.save(new Agricultor("Carlos Andrés Herrera", "2222222222",
                Municipio.PALMIRA, "313 555 0202", "carlos.herrera@fincalaesperanza.co", "Finca La Esperanza"));
        Agricultor luzDary = agricultorRepository.save(new Agricultor("Luz Dary Ramírez", "3333333333",
                Municipio.BUGA, "314 555 0303", "luz.ramirez@fincavillaluz.co", "Finca Villa Luz"));
        Agricultor jorge = agricultorRepository.save(new Agricultor("Jorge Eliécer Muñoz", "4444444444",
                Municipio.TULUA, "315 555 0404", "jorge.munoz@fincaelretiro.co", "Finca El Retiro"));
        Agricultor sandra = agricultorRepository.save(new Agricultor("Sandra Milena Torres", "5555555555",
                Municipio.CAICEDONIA, "316 555 0505", "sandra.torres@fincalaaurora.co", "Finca La Aurora"));
        Agricultor pedro = agricultorRepository.save(new Agricultor("Pedro Nel García", "6666666666",
                Municipio.JAMUNDI, "317 555 0606", "pedro.garcia@fincasanpedro.co", "Finca San Pedro"));

        Long compradorDemoId = sembrarUsuariosDemo();

        Producto pina = productoRepository.save(producto("Piña Gold", Categoria.FRUTAS, 500, "2500",
                "kg", maria, 5));
        productoRepository.save(producto("Aguacate Hass", Categoria.FRUTAS, 300, "6800", "kg", maria, 12));
        Producto tomate = productoRepository.save(producto("Tomate Chonto", Categoria.VERDURAS, 800, "3200",
                "kg", carlos, 3));
        productoRepository.save(producto("Cebolla Larga", Categoria.VERDURAS, 250, "4100", "kg", carlos, 4));
        productoRepository.save(producto("Café Pergamino", Categoria.CAFE, 600, "14500", "kg", luzDary, 20));
        productoRepository.save(producto("Maíz Amarillo", Categoria.GRANOS, 400, "2600", "kg", luzDary, 15));
        productoRepository.save(producto("Plátano Hartón", Categoria.OTROS, 1200, "2100", "kg", jorge, 6));
        productoRepository.save(producto("Yuca Industrial", Categoria.TUBERCULOS, 700, "1800", "kg", jorge, 8));
        productoRepository.save(producto("Uva Isabella", Categoria.FRUTAS, 400, "5200", "kg", sandra, 7));
        productoRepository.save(producto("Papa Criolla", Categoria.TUBERCULOS, 900, "2800", "kg", pedro, 9));
        productoRepository.save(producto("Frijol Cargamanto", Categoria.GRANOS, 150, "9800", "kg", pedro, 30));
        productoRepository.save(producto("Lechuga Batavia", Categoria.VERDURAS, 200, "1900", "kg", pedro, 2));

        Pedido pedido = new Pedido();
        pedido.setCompradorNombre("Restaurante La Huerta");
        pedido.setCompradorTelefono("318 555 0707");
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedido.setFechaCreacion(LocalDateTime.now().minusDays(1));
        pedido.setMunicipioDestino(Municipio.CALI.name());
        pedido.setFleteZona("CERCANA");
        pedido.setFleteCosto(new BigDecimal("7500.00"));
        pedido.setTotal(new BigDecimal("43500.00"));
        pedido.setUsuarioId(compradorDemoId);
        pedido.agregarItem(item(pedido, pina, 8, new BigDecimal("2500.00")));
        pedido.agregarItem(item(pedido, tomate, 5, new BigDecimal("3200.00")));
        Pago pago = new Pago();
        pago.setMedioPago(medioPagoRepository.findByNombre("TRANSFERENCIA").orElseThrow());
        pago.setMonto(pedido.getTotal());
        pago.setEstado("PAGADO");
        pago.setFechaPago(pedido.getFechaCreacion().plusHours(3));
        pedido.asignarPago(pago);
        Pedido guardado = pedidoRepository.save(pedido);
        pina.setCantidadDisponible(pina.getCantidadDisponible() - 8);
        tomate.setCantidadDisponible(tomate.getCantidadDisponible() - 5);
        notificacionRepository.save(new Notificacion(guardado.getId(), "Pedido creado con estado PENDIENTE"));
        notificacionRepository.save(new Notificacion(guardado.getId(),
                "El estado del pedido cambió a EN_PREPARACION"));
    }

    private Long sembrarUsuariosDemo() {
        Long agricultorId = agricultorRepository.findAll().stream()
                .map(Agricultor::getId)
                .findFirst()
                .orElse(null);
        Usuario comprador = usuarioRepository.findByEmail("comprador@agrovalle.co")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre("Comprador Demo")
                        .email("comprador@agrovalle.co")
                        .passwordHash(passwordEncoder.encode("Demo1234*"))
                        .telefono("300 000 0001")
                        .rol(Rol.COMPRADOR)
                        .build()));
        if (agricultorId != null && !usuarioRepository.existsByEmail("agricultor@agrovalle.co")
                && !usuarioRepository.existsByAgricultorId(agricultorId)) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Maria Fernanda Ospina")
                    .email("agricultor@agrovalle.co")
                    .passwordHash(passwordEncoder.encode("Demo1234*"))
                    .telefono("312 555 0101")
                    .rol(Rol.AGRICULTOR)
                    .agricultorId(agricultorId)
                    .build());
        }
        if (!usuarioRepository.existsByEmail("admin@agrovalle.co")) {
            usuarioRepository.save(Usuario.builder()
                    .nombre("Administrador AgroValle")
                    .email("admin@agrovalle.co")
                    .passwordHash(passwordEncoder.encode("Admin1234*"))
                    .rol(Rol.ADMIN)
                    .build());
        }
        return comprador.getId();
    }

    private Producto producto(String nombre, Categoria categoria, double cantidad, String precio,
            String unidad, Agricultor agricultor, long diasCosecha) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setCantidadDisponible(cantidad);
        producto.setPrecioUnitario(new BigDecimal(precio));
        producto.setUnidad(unidad);
        producto.setFechaCosecha(LocalDate.now().plusDays(diasCosecha));
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setDisponible(true);
        producto.setAgricultor(agricultor);
        return producto;
    }

    private ItemPedido item(Pedido pedido, Producto producto, double cantidad, BigDecimal precio) {
        ItemPedido item = new ItemPedido();
        item.setProducto(producto);
        item.setCantidad(cantidad);
        item.setPrecioUnitario(precio);
        item.setSubtotal(precio.multiply(BigDecimal.valueOf(cantidad)));
        item.setPedido(pedido);
        return item;
    }
}
