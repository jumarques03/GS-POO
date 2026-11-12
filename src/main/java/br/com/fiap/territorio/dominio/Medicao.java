package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Medicao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String sensorId;
	private String tipo;
	private double valor;
	private LocalDateTime dataHora;

	// Exigido pelo JPA para recriar o objeto a partir de uma linha lida do banco.
	protected Medicao() {
	}

	// O valor deve estar na unidade do sensor (pluviometro: mm/h).
	public Medicao(Sensor sensor, double valor) {
		validar(sensor, valor);
		this.sensorId = sensor.getId();
		this.tipo = sensor.getTipo();
		this.valor = valor;
		this.dataHora = LocalDateTime.now();
	}

	private static void validar(Sensor sensor, double valor) {
		if (sensor == null) {
			throw new DadosInvalidosException("Medicao sem sensor: o sensor nao pode ser nulo");
		}
		// Precisa vir antes da checagem de faixa: comparacoes com NaN sao sempre falsas e ele passaria por ela.
		if (Double.isNaN(valor) || Double.isInfinite(valor)) {
			throw new DadosInvalidosException("Medicao com valor invalido: " + valor + " (nao pode ser NaN nem infinito)");
		}
		if (valor < sensor.getFaixaMinima() || valor > sensor.getFaixaMaxima()) {
			throw new DadosInvalidosException("Medicao fora da faixa: " + sensor.getTipo() + " registrou " + valor
					+ " (faixa valida: " + sensor.getFaixaMinima() + " a " + sensor.getFaixaMaxima() + ")");
		}
	}

	public Long getId() {
		return id;
	}

	public String getSensorId() {
		return sensorId;
	}

	public String getTipo() {
		return tipo;
	}

	// Na unidade do sensor (pluviometro: mm/h).
	public double getValor() {
		return valor;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}
}
