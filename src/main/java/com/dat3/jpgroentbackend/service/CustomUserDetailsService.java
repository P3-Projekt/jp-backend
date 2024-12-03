package com.dat3.jpgroentbackend.service;


import com.dat3.jpgroentbackend.model.User;
import com.dat3.jpgroentbackend.model.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByName(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        User userDetails = user.get();

        // Brug en separat UserDetails-implementering her
        return org.springframework.security.core.userdetails.User
                .withUsername(userDetails.getName())
                .password(new BCryptPasswordEncoder().encode("MasterPassword")) // Husk at hash password i en rigtig implementering
                .roles(userDetails.getRole().name())
                .build();
    }
}
