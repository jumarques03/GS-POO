package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import org.junit.jupiter.api.Test;

class SensoresTest {

	private static final int QUANTIDADE_LEITURAS = 1000;

	@Test
	void nivelCorregoDeveLerValorDentroDaFaixa() {
		// Arrange
		NivelCorregoSensor sensor = new NivelCorregoSensor("CORR-01");

		// Act e Assert
		verificarLeiturasDentroDaFaixa(sensor);
	}

	@Test
	void umidadeSoloDeveLerValorDentroDaFaixa() {
		// Arrange
		UmidadeSoloSensor sensor = new UmidadeSoloSensor("UMID-01");

		// Act e Assert
		verificarLeiturasDentroDaFaixa(sensor);
	}

	@Test
	void nivelCorregoDeveLancarFonteIndisponivelExceptionQuandoInativo() {
		// Arrange
		NivelCorregoSensor sensor = new NivelCorregoSensor("CORR-01");
		sensor.setAtivo(false);

		// Act e Assert
		assertThrows(FonteIndisponivelException.class, sensor::lerValor);
	}

	@Test
	void umidadeSoloDeveLancarFonteIndisponivelExceptionQuandoInativo() {
		// Arrange
		UmidadeSoloSensor sensor = new UmidadeSoloSensor("UMID-01");
		sensor.setAtivo(false);

		// Act e Assert
		assertThrows(FonteIndisponivelException.class, sensor::lerValor);
	}

	private void verificarLeiturasDentroDaFaixa(Sensor sensor) {
		for (int i = 0; i < QUANTIDADE_LEITURAS; i++) {
			double valor = sensor.lerValor();

			assertTrue(valor >= sensor.getFaixaMinima() && valor <= sensor.getFaixaMaxima(),
					sensor.getTipo() + " fora da faixa na leitura " + i + ": " + valor);
		}
	}
}
