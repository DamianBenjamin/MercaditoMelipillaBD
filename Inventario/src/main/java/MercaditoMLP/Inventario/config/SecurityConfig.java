package MercaditoMLP.Inventario.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF ya que usaremos tokens JWT estatales
                .cors(cors -> cors.configure(http)) // Mantenemos habilitado el CrossOrigin para conectar con React
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin estado en el servidor
                .authorizeHttpRequests(auth -> auth
                        // Permite libre acceso a Swagger para documentar y al endpoint de autenticación que crearemos
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. 🌟 CONSULTAS PÚBLICAS (GET): Permitir explícitamente a ambos locales ANTES de bloquear los métodos de escritura
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("VENTAS", "PRODUCCION")
                        .requestMatchers("/api/notas/**").hasAnyRole("VENTAS", "PRODUCCION")

                        // 🛑 RESTRICCIONES DURAS DE CAPA DE NEGOCIO (RBAC)
                        // Modificaciones lógicas, borrados e ingresos: Exclusivos del Local 2
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("VENTAS")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("VENTAS")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("VENTAS")

                        // Las consultas de listas, reportes, alertas y notas: Accesibles por ambos locales
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("VENTAS", "PRODUCCION")
                        .requestMatchers("/api/notas/**").hasAnyRole("VENTAS", "PRODUCCION")

                        .anyRequest().authenticated()
                );
        http.addFilterBefore(jwtRequestFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}