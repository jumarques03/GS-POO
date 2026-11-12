package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

class PluviometroSensorTest {

	private static final int QUANTIDADE_LEITURAS = 1000;

	@Test
	void lerValorDeveFicarDentroDaFaixa() {
		// Arrange
		PluviometroSensor sensor = new PluviometroSensor("PLUV-01");

		for (int i = 0; i < QUANTIDADE_LEITURAS; i++) {
			// Act
			double valor = sensor.lerValor();

			// Assert
			assertTrue(valor >= sensor.getFaixaMinima() && valor <= sensor.getFaixaMaxima(),
					"Valor fora da faixa na leitura " + i + ": " + valor);
		}
	}

	@Test
	void deveLancarFonteIndisponivelExceptionQuandoSensorInativo() {
		// Arrange
		PluviometroSensor sensor = new PluviometroSensor("PLUV-01");
		sensor.setAtivo(false);

		// Act
		FonteIndisponivelException erro = assertThrows(FonteIndisponivelException.class, sensor::lerValor);

		// Assert
		assertEquals("Sensor indisponivel: PLUV-01 (PLUVIOMETRO) esta inativo ou com falha", erro.getMessage());
	}
}
