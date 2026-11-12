package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoMediaMovelTest {

	private static final double DELTA = 0.001;

	private final PluviometroSensor sensor = new PluviometroSensor("PLUV-01");

	@Test
	void deveCalcularMediaDasUltimasLeituras() {
		// Arrange
		PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
		List<Medicao> historico = List.of(new Medicao(sensor, 10.0), new Medicao(sensor, 20.0),
				new Medicao(sensor, 30.0), new Medicao(sensor, 40.0), new Medicao(sensor, 50.0));

		// Act
		double resultado = previsao.prever(historico);

		// Assert
		assertEquals(30.0, resultado, DELTA);
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoMenosDeCincoLeituras() {
		// Arrange
		PrevisaoMediaMovel previsao = new PrevisaoMediaMovel();
		List<Medicao> historico = List.of(new Medicao(sensor, 10.0), new Medicao(sensor, 20.0),
				new Medicao(sensor, 30.0));

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> previsao.prever(historico));

		// Assert
		assertEquals("Historico insuficiente: 3 leituras, minimo 5", erro.getMessage());
	}
}
