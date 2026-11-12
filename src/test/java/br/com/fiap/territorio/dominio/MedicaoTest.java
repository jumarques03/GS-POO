package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class MedicaoTest {

	@Test
	void deveCriarMedicaoQuandoValorDentroDaFaixa() {
		// Arrange
		PluviometroSensor sensor = new PluviometroSensor("PLUV-01");
		double valorValido = 50.0;

		// Act
		Medicao medicao = new Medicao(sensor, valorValido);

		// Assert
		assertEquals("PLUV-01", medicao.getSensorId());
		assertEquals("PLUVIOMETRO", medicao.getTipo());
		assertEquals(valorValido, medicao.getValor());
		assertNotNull(medicao.getDataHora());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoValorNegativo() {
		// Arrange
		PluviometroSensor sensor = new PluviometroSensor("PLUV-01");
		double valorNegativo = -5.0;

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> new Medicao(sensor, valorNegativo));

		// Assert
		assertEquals("Medicao fora da faixa: PLUVIOMETRO registrou -5.0 (faixa valida: 0.0 a 150.0)",
				erro.getMessage());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoValorAcimaDaFaixa() {
		// Arrange
		PluviometroSensor sensor = new PluviometroSensor("PLUV-01");
		double valorAcimaDaFaixa = 200.0;

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> new Medicao(sensor, valorAcimaDaFaixa));

		// Assert
		assertEquals("Medicao fora da faixa: PLUVIOMETRO registrou 200.0 (faixa valida: 0.0 a 150.0)",
				erro.getMessage());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoSensorNulo() {
		// Arrange
		Sensor sensorNulo = null;
		double valor = 10.0;

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> new Medicao(sensorNulo, valor));

		// Assert
		assertEquals("Medicao sem sensor: o sensor nao pode ser nulo", erro.getMessage());
	}
}
