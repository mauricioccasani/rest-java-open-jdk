package com.mauricio.ccasani.rest.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mauricio.ccasani.rest.bean.Cliente;
import com.mauricio.ccasani.rest.dao.ClienteDao;
import com.mauricio.ccasani.rest.service.exc.ExceptionService;
import com.mauricio.ccasani.rest.service.inf.IClienteServiceInf;

import lombok.Data;

@Service
@Data
public class ClienteServiceImpl implements IClienteServiceInf {
	@Autowired
	private ClienteDao clienteDao;

	@Override
	public List<Cliente> findAll() throws ExceptionService {
		try {
			return this.getClienteDao().findAll();
		} catch (Exception e) {
			throw new ExceptionService("Error al listar clientes " + e.getMessage());
		}
	}

	@Override
	public Page<Cliente> findAll(Pageable pageable) throws ExceptionService {
		try {
			return this.getClienteDao().findAll(pageable);
		} catch (Exception e) {
			throw new ExceptionService("Error al listar "+e.getMessage());
		}
		// TODO Auto-generated method stub
	
	}
	@Override
	public Cliente save(Cliente t) throws ExceptionService {
		try {
			return this.getClienteDao().save(t);
		} catch (Exception e) {
			throw new ExceptionService("Error al  registrar cliente " + e.getMessage());
		}
	}

	@Override
	public Cliente findById(Integer id) throws ExceptionService {
		try {
			Cliente cliente = new Cliente();
			Optional<Cliente> optCliente = this.getClienteDao().findById(id);
			if (optCliente.isPresent()) {
				cliente = optCliente.get();
			}
			return cliente;
		} catch (Exception e) {
			throw new ExceptionService("Error al  buscar id cliente " + e.getMessage());
		}
	}

	@Override
	public void deleteById(Integer id) throws ExceptionService {
		try {
			this.getClienteDao().deleteById(id);
		} catch (Exception e) {
			throw new ExceptionService("Error al  eliminar cliente " + e.getMessage());
		}
	}

	@Override
	public boolean existsByEmail(String email) throws ExceptionService {
		return this.getClienteDao().existsByEmail(email);
	}

	@Override
	public Optional<Cliente> findByEmail(String email) throws ExceptionService {
		try {
			return this.getClienteDao().findByEmail(email);
		} catch (Exception e) {
			throw new ExceptionService("Error al  buscar por email al cliente " + e.getMessage());
		}
	}

	

}
