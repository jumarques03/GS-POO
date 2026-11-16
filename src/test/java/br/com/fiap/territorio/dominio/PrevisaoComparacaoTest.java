package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrevisaoComparacaoTest {

	private static final int JANELA = 5;
	private static final double DELTA = 0.001;

	private final PluviometroSensor sensor = new PluviometroSensor("PLUV-01");

	@Test
	void deveProjetarTendenciaDeAlta() {
		// Arrange
		PrevisaoComparacao previsao = new PrevisaoComparacao();
		List<Medicao> historico = criarHistorico(60.0, 40.0);

		// Act
		double resultado = previsao.prever(historico);

		// Assert
		assertEquals(80.0, resultado, DELTA);
	}

	@Test
	void deveRetornarZeroQuandoTendenciaNegativa() {
		// Arrange
		PrevisaoComparacao previsao = new PrevisaoComparacao();
		List<Medicao> historico = criarHistorico(0.0, 50.0);

		// Act
		double resultado = previsao.prever(historico);

		// Assert
		assertEquals(0.0, resultado, DELTA);
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoMenosDeDezLeituras() {
		// Arrange
		PrevisaoComparacao previsao = new PrevisaoComparacao();
		List<Medicao> historico = criarHistorico(60.0, 40.0).subList(0, 9);

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> previsao.prever(historico));

		// Assert
		assertEquals("Historico insuficiente: 9 leituras, minimo 10", erro.getMessage());
	}

	// Lista da mais recente para a mais antiga: primeiro a janela recente, depois a anterior.
	private List<Medicao> criarHistorico(double valorRecente, double valorAnterior) {
		List<Medicao> historico = new ArrayList<>();
		for (int i = 0; i < JANELA; i++) {
			historico.add(new Medicao(sensor, valorRecente));
		}
		for (int i = 0; i < JANELA; i++) {
			historico.add(new Medicao(sensor, valorAnterior));
		}
		return historico;
	}
}
