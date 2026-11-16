package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import java.util.Random;

public class NivelCorregoSensor implements Sensor {

	public static final String TIPO = "NIVEL_CORREGO";

	// Faixa do nivel do corrego em cm (centimetros).
	private static final double FAIXA_MIN = 0.0;
	private static final double FAIXA_MAX = 400.0;
	private static final double ESCALA_UMA_CASA = 10.0;

	private final String id;
	private boolean ativo = true;
	private final Random random = new Random();

	public NivelCorregoSensor(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTipo() {
		return TIPO;
	}

	// Devolve o nivel do corrego em cm (centimetros), com 1 casa decimal.
	@Override
	public double lerValor() {
		if (!verificarFuncionamento()) {
			throw new FonteIndisponivelException("Sensor indisponivel: " + id + " (" + TIPO + ") esta inativo ou com falha");
		}
		double valor = FAIXA_MIN + random.nextDouble() * (FAIXA_MAX - FAIXA_MIN);
		return Math.round(valor * ESCALA_UMA_CASA) / ESCALA_UMA_CASA;
	}

	@Override
	public boolean verificarFuncionamento() {
		return ativo;
	}

	@Override
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	// Menor nivel valido, em cm.
	@Override
	public double getFaixaMinima() {
		return FAIXA_MIN;
	}

	// Maior nivel valido, em cm.
	@Override
	public double getFaixaMaxima() {
		return FAIXA_MAX;
	}
}
