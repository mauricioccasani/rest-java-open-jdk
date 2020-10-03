package com.mauricio.ccasani.rest.api;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mauricio.ccasani.rest.bean.Cliente;
import com.mauricio.ccasani.rest.service.exc.ExceptionService;
import com.mauricio.ccasani.rest.service.inf.IClienteServiceInf;

import lombok.Data;

@SuppressWarnings("serial")
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api-v1")

@Data
public class ClienteController implements Serializable {
	@Autowired
	private IClienteServiceInf clienteService;

	@GetMapping("/clientes")
	public List<Cliente> listarCliente() {
		List<Cliente> listarCliente = null;
		try {
			listarCliente = this.getClienteService().findAll();
		} catch (ExceptionService e) {
			e.printStackTrace();
		}
		return listarCliente;
	}

	@GetMapping("/clientes/page/{page}")
	public Page<Cliente> listarCliente(@PathVariable Integer page) {
		Page<Cliente> listarCliente = null;
		Pageable pageable = PageRequest.of(page, 5);
		try {
			listarCliente = this.getClienteService().findAll(pageable);
		} catch (ExceptionService e) {
			e.printStackTrace();
		}
		return listarCliente;
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> buscarXid(@PathVariable Integer id) {
		Map<String, Object> response = new HashMap<String, Object>();
		Cliente cliente = null;
		try {
			cliente = this.getClienteService().findById(id);
			if (cliente.getId() == null) {
				response.put("mensaje", "El cliente id: ".concat(id.toString().concat(" no existe")));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}

		} catch (ExceptionService e) {
			response.put("error", e.getMessage());
		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}

	@PostMapping("/clientes")
	public ResponseEntity<?> registrar(@Valid @RequestBody Cliente cliente, BindingResult result)
			throws ExceptionService {
		Cliente oCliente = null;
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			if (result.hasErrors()) {
				List<String> errors = new ArrayList<>();
				// result.getFieldErrors();
				for (FieldError err : result.getFieldErrors()) {
					errors.add("El campo ' " + err.getField() + " ' " + err.getDefaultMessage());
				}
				response.put("errors", errors);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (StringUtils.isAllBlank(cliente.getNombres())) {
				response.put("mensaje", "el campo nombre es obligatorio");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			if (StringUtils.isAllBlank(cliente.getApellidos())) {
				response.put("mensaje", "el campo apellidos es obligatorio");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (StringUtils.isAllBlank(cliente.getEmail())) {
				response.put("mensaje", "el campo email es obligatorio");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			if (this.getClienteService().existsByEmail(cliente.getEmail())) {
				response.put("mensaje", "El : " + cliente.getEmail().concat(" ya existe"));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			}
			oCliente = this.getClienteService().save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error de insercion cliente");
			response.put("error", e.getMessage().concat(" : ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);

		}
		response.put("mensaje", "El cliente ha sido creado con exito");
		response.put("cliente", oCliente);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> actualizar(@Valid @RequestBody Cliente cliente, BindingResult result,
			@PathVariable Integer id) throws ExceptionService {

		Map<String, Object> response = new HashMap<String, Object>();
		Cliente clienteActual = this.getClienteService().findById(id);
		if (result.hasErrors()) {
			List<String> errors = new ArrayList<>();
			// result.getFieldErrors();
			for (FieldError err : result.getFieldErrors()) {
				errors.add("El campo ' " + err.getField() + " ' " + err.getDefaultMessage());
			}
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (this.getClienteService().existsByEmail(cliente.getEmail())
				&& this.getClienteService().findByEmail(cliente.getEmail()).get().getId()!=clienteActual.getId()) {
			response.put("mensaje", "El : " + cliente.getEmail().concat(" ya existe"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		if (clienteActual.getId() == null) {
			response.put("mensaje", "el campo id no existe");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		if (StringUtils.isAllBlank(cliente.getNombres())) {
			response.put("mensaje", "el campo nombre es obligatorio");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (StringUtils.isAllBlank(cliente.getApellidos())) {
			response.put("mensaje", "el campo apellidos es obligatorio");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (StringUtils.isAllBlank(cliente.getEmail())) {
			response.put("mensaje", "el campo email es obligatorio");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {

			clienteActual = getClienteService().save(cliente);
		} catch (ExceptionService e) {
			response.put("mensaje", "Error de insercion cliente");
			response.put("error", e.getMessage().concat(" : ").concat(e.getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente ha sido actualizado con exito");
		response.put("cliente", clienteActual);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Integer id) throws ExceptionService {
		Map<String, Object> response = new HashMap<String, Object>();
		// Cliente cliente = this.getClienteService().findById(id);
		try {
			Cliente clienteId = this.getClienteService().findById(id);
			String nombreFotoAnterior = clienteId.getFoto();
			if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
				Path rutaFotoAnterio = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File achivoFotoAnterior = rutaFotoAnterio.toFile();
				if (achivoFotoAnterior.exists() && achivoFotoAnterior.canRead()) {
					achivoFotoAnterior.delete();
				}
			}

			if (clienteId.getId() == null) {
				response.put("mensaje", "el campo id no existe");
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			this.getClienteService().deleteById(id);

		} catch (Exception e) {
			response.put("mensaje", "Error de consulta" + e.getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido eliminado con exito");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@PostMapping("/clientes/upload")
	public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Integer id)
			throws ExceptionService {
		Map<String, Object> response = new HashMap<String, Object>();
		Cliente cliente = this.getClienteService().findById(id);
		if (!archivo.isEmpty()) {

			String nombreArchivo = UUID.randomUUID().toString() + archivo.getOriginalFilename().replace(" ", "");
			Path rutaArchivo = Paths.get("uploads").resolve(nombreArchivo).toAbsolutePath();
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);
			} catch (IOException e) {
				response.put("mensaje", "Error de consulta" + e.getMessage());
				response.put("error", e.getMessage().concat(" : ").concat(e.getMessage()));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String nombreFotoAnterior = cliente.getFoto();
			if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0) {
				Path rutaFotoAnterio = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
				File achivoFotoAnterior = rutaFotoAnterio.toFile();
				if (achivoFotoAnterior.exists() && achivoFotoAnterior.canRead()) {
					achivoFotoAnterior.delete();
				}
			}
			cliente.setFoto(nombreArchivo);
			this.getClienteService().save(cliente);
			response.put("cliente", cliente);
			response.put("mensaje", "Has subido correctamente la imagen " + nombreArchivo);
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto) {

		Path rutaArchivo = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
		Resource recurso = null;
		try {
			recurso = new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (!recurso.exists()&& !recurso.isReadable()) {
			throw new RuntimeException("Error al cargar imagen "+nombreFoto);
		}
		HttpHeaders cabecera =new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "Attachment; filename=\""+recurso.getFilename()+"\""); 
		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
}
