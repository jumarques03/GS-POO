package br.com.fiap.territorio.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

class LimiaresTest {

	@Test
	void deveCriarLimiaresValidos() {
		// Arrange
		double atencao = 20.0;
		double alerta = 50.0;
		double critico = 80.0;

		// Act
		Limiares limiares = new Limiares(atencao, alerta, critico);

		// Assert
		assertEquals(atencao, limiares.getAtencao());
		assertEquals(alerta, limiares.getAlerta());
		assertEquals(critico, limiares.getCritico());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoForaDeOrdem() {
		// Arrange
		double atencao = 50.0;
		double alerta = 20.0;
		double critico = 80.0;

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> new Limiares(atencao, alerta, critico));

		// Assert
		assertEquals("Limiares invalidos: atencao=50.0, alerta=20.0, critico=80.0 (exigido: 0 < atencao < alerta < critico)",
				erro.getMessage());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoAtencaoZero() {
		// Arrange
		double atencao = 0.0;

		// Act e Assert
		assertThrows(DadosInvalidosException.class, () -> new Limiares(atencao, 50.0, 80.0));
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoAtencaoNegativa() {
		// Arrange
		double atencao = -5.0;

		// Act e Assert
		assertThrows(DadosInvalidosException.class, () -> new Limiares(atencao, 50.0, 80.0));
	}
}
