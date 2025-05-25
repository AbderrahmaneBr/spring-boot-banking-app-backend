## Documentation

### Structure des dossiers

Le projet suit une structure modulaire et claire :

```
spring-boot-banking-app-backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banking/
│   │   │           ├── controller/      # Contrôleurs REST
│   │   │           ├── service/         # Logique métier
│   │   │           ├── dto/             # Objets de transfert de données (DTO)
│   │   │           ├── mapper/          # Convertisseurs entités <-> DTO
│   │   │           ├── model/           # Entités JPA (modèle)
│   │   │           └── repository/      # Accès aux données (JPA Repository)
│   │   └── resources/
│   │       └── application.properties   # Configuration
│   └── test/
│       └── java/                        # Tests unitaires et d’intégration
└── pom.xml                              # Dépendances Maven
```

### Architecture du modèle et justification

L’architecture repose sur la séparation des responsabilités (inspirée du Domain Driven Design).  
Les entités principales (`Compte`, `Client`, `Transaction`, etc.) sont dans `model`.  
Ce choix permet :

- Une meilleure lisibilité et organisation du code.
- Une testabilité accrue (chaque couche est testable indépendamment).
- Une évolutivité facilitée (ajout de fonctionnalités sans impacter l’ensemble).

### Différences entre les stratégies de mapping JPA

- **Single Table** :  
  Toutes les classes héritées sont stockées dans une seule table avec une colonne discriminante.  
  *Avantages* : rapide, simple.  
  *Inconvénients* : beaucoup de colonnes nulles, difficile à maintenir si la hiérarchie grandit.

- **Table per Class** :  
  Chaque classe concrète a sa propre table avec toutes ses propriétés.  
  *Avantages* : pas de colonnes inutilisées.  
  *Inconvénients* : duplication de colonnes, requêtes polymorphes complexes.

- **Joined Table** :  
  Une table par classe, reliées par des clés étrangères.  
  *Avantages* : normalisation, pas de redondance.  
  *Inconvénients* : requêtes plus lentes (jointures), complexité accrue.

_Dans une application bancaire, la stratégie « Joined Table » est souvent privilégiée pour sa flexibilité et sa normalisation._

### Description des couches

- **Controllers** (`controller/`) :  
  Exposent les endpoints REST, reçoivent les requêtes, valident les entrées, appellent les services.

- **Services** (`service/`) :  
  Contiennent la logique métier, orchestrent les opérations, appliquent les règles de gestion.

- **DTO** (`dto/`) :  
  Objets servant à transférer les données entre les couches, exposent uniquement les informations nécessaires à l’API.

- **Mappers** (`mapper/`) :  
  Convertissent les entités JPA en DTO et inversement (via MapStruct ou méthodes manuelles).

### Pourquoi utiliser des DTO?

- **Sécurité** : on ne révèle pas la structure interne de la base de données.
- **Contrôle** : on expose uniquement les champs nécessaires à l’API.
- **Évolution** : on peut faire évoluer l’API sans impacter la structure interne.
- **Validation** : les DTO peuvent intégrer des règles de validation spécifiques.

**En résumé** :  
L’utilisation de DTO améliore la sécurité, la maintenabilité et la clarté du code, contrairement à l’exposition directe des entités.


### Ajout de la couche Sécurité :

Pour sécuriser notre application, il faut ajouter la couche sécurité.
Dans notre cas, on va ajouter l'authentification en utilisant JWT.

JWT (JSON Web Token) est un format de jeton sécurisé utilisé pour transmettre des informations entre deux parties, souvent pour l’authentification et l’autorisation, sous forme d’un objet JSON signé.

Pour implémenter ce dernier, il faut tout d'abord ajouter la bibliothèque suivante:
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-authorization-server</artifactId>
    <version>1.4.3</version>
</dependency>
```

Ajouter une clé secrete de hashage :
```properties
jwt.secret=256bit_clé
```

Puis ajouter les méthodes suivantes dans le fichier SecurityConfig :
```java
public class SecurityConfig {
  ...
    @Value("${jwt.secret}")
    private String secretKey;
  ...
  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtEncoder jwtEncoder(){
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getBytes()));
  }

  @Bean
  public JwtDecoder jwtDecoder(){
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "RSA");
    return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
  }
}
```
Ces méthodes permettent de générer/décoder un token JWT.

Maintenant, il faut ajouter la logique d'authentification, dans notre cas, on veut que les informations se stockent dans la base de données.

On définit une entité ```User``` :
```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String username;
    @Column(unique = true, nullable = false)
    String email;
    @Column(nullable = false)
    String password;

    @Enumerated(EnumType.STRING)
    private UserRoles role;
}
```
Interface ```UserRepository``` :
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```
Enumération ```UserRoles``` :
```java
public enum UserRoles {
    USER, ADMIN
}
```
Puis, on doit définir notre implémentation personnalisée de ```UserDetailsService``` :
```java
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
```
Maintenant, il faut définir la logique d'authentification :
```java
@Bean
public AuthenticationManager authenticationManager( @Qualifier("customUserDetailsService") UserDetailsService userDetailsService){
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    return new ProviderManager(daoAuthenticationProvider);
}

@Bean
public UserDetailsManager userDetailsManager(PasswordEncoder passwordEncoder) {
    return new InMemoryUserDetailsManager();
}

@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
    converter.setAuthorityPrefix("");
    converter.setAuthoritiesClaimName("scope");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
    return jwtAuthenticationConverter;
}
```
- ```userDetailsManager```: Crée un ```InMemoryUserDetailsManager```, une implémentation de ```UserDetailsManager``` qui stocke les utilisateurs en mémoire.
- ```jwtAuthenticationConverter```: Convertit un JWT (JSON Web Token) en un objet ```Authentication``` pour Spring Security.
- ```authenticationManager```: Crée et configure un ```AuthenticationManager``` personnalisé avec un ```DaoAuthenticationProvider```.

Pour qu'on puisse accéder aux routes d'authentification sans être bloqué, il faut ajouter les filtres suivants :
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(ar -> ar.requestMatchers(
                    "/auth/login/**",
                    "/auth/register/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-resources/**",
                    "/webjars/**"
            ).permitAll().anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
            )
    ;
    return httpSecurity.build();
}
```

Finalement, dans notre ```controllers/auth```, on ajoute nos controllers :
```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public Authentication authentication(Authentication authentication){
        return authentication;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Instant instant = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                .subject(request.getUsername())
                .claim("userId", user.getId())
                .claim("scope", scope)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet
        );

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access-token", jwt);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody CreateUserDTO request) {
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(request.getUsername());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User already exists"));
            }

            // Save user to DB
            User newUser = User.builder()
                    .username(request.getUsername())
                    .email(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(UserRoles.valueOf(request.getRoles().get(0)))
                    .build();

            userRepository.save(newUser);

            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully",
                    "username", newUser.getUsername(),
                    "roles", newUser.getRole()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

}
```

### Problème des CORS :
Pour éviter le problème des CORS, il faut ajouter cette annotation en haut de chaque controller :
```java
@CrossOrigin("*")
```
ou
Ajouter cette fonction dans ```SecurityConfig``` :
```java
@Bean
public CorsConfigurationSource corsConfigurationSource(){
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
}
```