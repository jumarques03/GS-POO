package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PontoVulneravelTest {

	@Test
	void deveOperarQuandoTodosOsSensoresFuncionam() {
		// Arrange
		List<Sensor> sensores = List.of(new PluviometroSensor("PLUV-01"), new NivelCorregoSensor("CORR-01"),
				new UmidadeSoloSensor("UMID-01"));
		PontoVulneravel ponto = new PontoVulneravel("PV-01", "Passagem subterranea", "Zona Sul", sensores);

		// Act
		String status = ponto.verificarStatus();

		// Assert
		assertEquals("OPERANDO", status);
		assertEquals("OPERANDO", ponto.getStatus());
	}

	@Test
	void deveFalharQuandoUmSensorInativo() {
		// Arrange
		NivelCorregoSensor corrego = new NivelCorregoSensor("CORR-01");
		corrego.setAtivo(false);
		List<Sensor> sensores = List.of(new PluviometroSensor("PLUV-01"), corrego);
		PontoVulneravel ponto = new PontoVulneravel("PV-01", "Passagem subterranea", "Zona Sul", sensores);

		// Act
		String status = ponto.verificarStatus();

		// Assert
		assertEquals("COM FALHA", status);
		assertEquals("COM FALHA", ponto.getStatus());
	}
}
