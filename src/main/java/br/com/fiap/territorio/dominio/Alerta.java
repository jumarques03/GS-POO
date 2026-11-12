package br.com.fiap.territorio.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Alerta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sensorId;
	@Enumerated(EnumType.STRING)
	private NivelAlerta nivel;
	private double valorPrevisto;
	private double limite;
	private String mensagem;
	private LocalDateTime dataHora;

	// Exigido pelo JPA para recriar o objeto a partir de uma linha lida do banco.
	protected Alerta() {
	}

	// valorPrevisto e limite na unidade do sensor (pluviometro: mm/h).
	public Alerta(String sensorId, NivelAlerta nivel, double valorPrevisto, double limite) {
		this.sensorId = sensorId;
		this.nivel = nivel;
		this.valorPrevisto = valorPrevisto;
		this.limite = limite;
		this.mensagem = "Sensor " + sensorId + ": valor previsto " + valorPrevisto
				+ " superou o limite de " + nivel + " (" + limite + ")";
		this.dataHora = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getSensorId() {
		return sensorId;
	}

	public NivelAlerta getNivel() {
		return nivel;
	}

	public double getValorPrevisto() {
		return valorPrevisto;
	}

	public double getLimite() {
		return limite;
	}

	public String getMensagem() {
		return mensagem;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}
}
