package com.mauricio.ccasani.rest.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;// =Integer.parseInt(UUID.randomUUID().toString());
	@NotBlank(message = "No puede estar vacio")
	@Size(min = 3, max = 10, message = "El tamaño tiene que estar entre 3 y 10 caracteres")
	@Column(nullable = false)
	private String nombres;
	@NotBlank(message = "No puede estar vacio")
	@Size(min = 3, max = 10, message = "El tamaño tiene que estar entre 3 y 10 caracteres")
	@Column(nullable = false)
	private String apellidos;
	@Email(message = "no es una direccion de correo valido")
	@NotBlank(message = "No puede estar vacio")
	@Column(nullable = false, unique = true)
	private String email;
	@Temporal(TemporalType.DATE)

	@NotNull(message = "No  puedes  estar vacio")

	private Date fecha;
	
	private String foto;

}
