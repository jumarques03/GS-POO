package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class DadosTerritorioTest {

	private static final int POPULACAO = 5000;

	@Test
	void deveAlterarPopulacaoExposta() {
		// Arrange
		DadosTerritorio dados = new DadosTerritorio(POPULACAO);

		// Act
		dados.setPopulacaoExposta(8000);

		// Assert
		assertEquals(8000, dados.getPopulacaoExposta());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoPopulacaoNegativa() {
		// Arrange
		DadosTerritorio dados = new DadosTerritorio(POPULACAO);

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> dados.setPopulacaoExposta(-1));

		// Assert
		assertEquals("Populacao exposta invalida: -1 (nao pode ser negativa)", erro.getMessage());
		assertEquals(POPULACAO, dados.getPopulacaoExposta());
	}

	@Test
	void deveGuardarLimiaresPorTipoDeSensor() {
		// Arrange
		DadosTerritorio dados = new DadosTerritorio(POPULACAO);

		// Act
		dados.definirLimiares(PluviometroSensor.TIPO, 20.0, 50.0, 80.0);
		dados.definirLimiares(NivelCorregoSensor.TIPO, 150.0, 250.0, 320.0);

		// Assert
		assertEquals(50.0, dados.getLimiares(PluviometroSensor.TIPO).getAlerta());
		assertEquals(250.0, dados.getLimiares(NivelCorregoSensor.TIPO).getAlerta());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoLimiaresInvalidos() {
		// Arrange
		DadosTerritorio dados = new DadosTerritorio(POPULACAO);

		// Act e Assert
		assertThrows(DadosInvalidosException.class,
				() -> dados.definirLimiares(PluviometroSensor.TIPO, 80.0, 50.0, 20.0));
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoSensorSemLimiares() {
		// Arrange
		DadosTerritorio dados = new DadosTerritorio(POPULACAO);

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> dados.getLimiares(UmidadeSoloSensor.TIPO));

		// Assert
		assertEquals("Sem limiares definidos para o sensor: UMIDADE_SOLO", erro.getMessage());
	}
}
