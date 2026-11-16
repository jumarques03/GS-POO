package br.com.fiap.territorio.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.territorio.dominio.EstrategiaPrevisao;
import br.com.fiap.territorio.dominio.PrevisaoComparacao;
import br.com.fiap.territorio.dominio.PrevisaoMediaMovel;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class FabricaPrevisaoTest {

	@Test
	void deveCriarPrevisaoMediaMovel() {
		// Arrange
		String tipo = FabricaPrevisao.MEDIA_MOVEL;

		// Act
		EstrategiaPrevisao estrategia = FabricaPrevisao.criar(tipo);

		// Assert
		assertInstanceOf(PrevisaoMediaMovel.class, estrategia);
	}

	@Test
	void deveCriarPrevisaoComparacao() {
		// Arrange
		String tipo = FabricaPrevisao.COMPARACAO;

		// Act
		EstrategiaPrevisao estrategia = FabricaPrevisao.criar(tipo);

		// Assert
		assertInstanceOf(PrevisaoComparacao.class, estrategia);
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoTipoDesconhecido() {
		// Arrange
		String tipoDesconhecido = "REGRESSAO";

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> FabricaPrevisao.criar(tipoDesconhecido));

		// Assert
		assertEquals("Tipo de previsao desconhecido: REGRESSAO (opcoes validas: MEDIA_MOVEL, COMPARACAO)",
				erro.getMessage());
	}
}
