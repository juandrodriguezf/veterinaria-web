package com.vetopia.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vetopia.entities.UserEntity;
import com.vetopia.repository.UserRepository;

/**
 * SERVICIO - CustomUserDetailsService
 * Carga la cuenta unificada (users) para Spring Security y traduce sus
 * roles (roles/user_roles) a autoridades ROLE_*.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity cuenta = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No encontramos ninguna cuenta con el usuario \"" + username + "\"."));

        String[] autoridades = cuenta.getRoles().stream()
                .map(rol -> rol.getNombre())
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.builder()
                .username(cuenta.getUsername())
                .password(cuenta.getPassword())
                .authorities(autoridades)
                .disabled(!cuenta.isActivo())
                .build();
    }
}
