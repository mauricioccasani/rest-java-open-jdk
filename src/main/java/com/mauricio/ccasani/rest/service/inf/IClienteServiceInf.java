package com.mauricio.ccasani.rest.service.inf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mauricio.ccasani.rest.bean.Cliente;
import com.mauricio.ccasani.rest.service.exc.ExceptionService;

public interface IClienteServiceInf extends GenericoService<Cliente, Integer> {
	boolean existsByEmail(String email) throws ExceptionService;
	Page<Cliente> findAll(Pageable pageable) throws ExceptionService;
}
