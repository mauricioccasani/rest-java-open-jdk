package com.mauricio.ccasani.rest.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mauricio.ccasani.rest.bean.Cliente;
import com.mauricio.ccasani.rest.service.exc.ExceptionService;
@Repository
public interface ClienteDao extends JpaRepository<Cliente, Integer> {
	boolean existsByEmail(String email);
	Optional<Cliente> findByEmail(String email);

}
