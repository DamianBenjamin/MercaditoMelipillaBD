package MercaditoMLP.Inventario.controller;

import MercaditoMLP.Inventario.config.JwtUtil;
import MercaditoMLP.Inventario.dto.LoginRequest;
import MercaditoMLP.Inventario.model.Usuario;
import MercaditoMLP.Inventario.repository.UsuarioRepository;
import MercaditoMLP.Inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            // Spring Security valida automáticamente si las credenciales coinciden con Neon
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Usuario o contraseña incorrectos");
            return ResponseEntity.status(401).body(error);
        }

        // Si la clave es correcta, cargamos el usuario y generamos su Token JWT
        final UserDetails userDetails = usuarioService.loadUserByUsername(loginRequest.getUsername());
        final String jwt = jwtUtil.generarToken(userDetails);

        // Buscamos los datos adicionales (como el nombre del Local y el rol) para enviárselos a React
        Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        if (usuario != null) {
            response.put("rol", usuario.getRol());
            response.put("nombreLocal", usuario.getNombreLocal());
            response.put("username", usuario.getUsername());
        }

        return ResponseEntity.ok(response);
    }
}