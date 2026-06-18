package MercaditoMLP.Inventario.config;

import MercaditoMLP.Inventario.model.Usuario;
import MercaditoMLP.Inventario.repository.UsuarioRepository;
import MercaditoMLP.Inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // Si no existen usuarios en Neon, los creamos por defecto
        if (usuarioRepository.count() == 0) {

            // 🏭 LOCAL 1: Fábrica (Solo ver listas, alertas y notas)
            Usuario fabrica = new Usuario();
            fabrica.setUsername("local1");
            fabrica.setPassword("dulcinea123"); // Se guardará encriptada automáticamente
            fabrica.setNombreLocal("Fábrica (Local 1)");
            fabrica.setRol("ROLE_PRODUCCION");
            usuarioService.registrarUsuario(fabrica);

            // 🍰 LOCAL 2: Ventas (Control total: ingresar, eliminar, trozar)
            Usuario ventas = new Usuario();
            ventas.setUsername("local2");
            ventas.setPassword("ventas123");
            ventas.setNombreLocal("Sucursal Ventas (Local 2)");
            ventas.setRol("ROLE_VENTAS");
            usuarioService.registrarUsuario(ventas);

            System.out.println("👉 [DataInitializer] Cuentas de acceso creadas con éxito en Neon.");
        }
    }
}