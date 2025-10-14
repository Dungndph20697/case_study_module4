package com.codegym.case_study_module4.service.impl;

import com.codegym.case_study_module4.model.Users;
import com.codegym.case_study_module4.repository.UserRepository;
import com.codegym.case_study_module4.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Users> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<Users> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Users save(Users users) {
        return userRepository.save(users);
    }

    @Override
    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<Users> findByEmailIgnoreCaseInternal(String email) {
        return Optional.ofNullable(userRepository.findByEmailIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public Optional<Users> findByEmailIgnoreCase(String email) {
        return Optional.ofNullable(userRepository.findByEmailIgnoreCase(email));
    }

    @Override
    public boolean existsByCitizenIdNumber(String citizenIdNumber) {
        if (citizenIdNumber == null) return false;
        return userRepository.existsByCitizenIdNumber(citizenIdNumber);
    }

    public Optional<Users> findByCitizenIdNumber(String citizenIdNumber) {
        return Optional.ofNullable(userRepository.findByCitizenIdNumber(citizenIdNumber));
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Users user = userRepository.findUsersByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
