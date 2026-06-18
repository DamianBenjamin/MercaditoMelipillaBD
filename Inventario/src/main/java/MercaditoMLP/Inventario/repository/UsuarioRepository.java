package MercaditoMLP.Inventario.repository;

import MercaditoMLP.Inventario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método clave para la autenticación: buscar por nombre de usuario
    Optional<Usuario> findByUsername(String username);
}