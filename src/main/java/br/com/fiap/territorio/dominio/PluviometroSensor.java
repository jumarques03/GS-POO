package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import java.util.Random;

public class PluviometroSensor implements Sensor {

	// Faixa de chuva em mm/h (milimetros por hora).
	private static final double FAIXA_MIN = 0.0;
	private static final double FAIXA_MAX = 150.0;
	private static final String TIPO = "PLUVIOMETRO";
	private static final double ESCALA_UMA_CASA = 10.0;

	private final String id;
	private boolean ativo = true;
	private final Random random = new Random();

	public PluviometroSensor(String id) {
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

	// Devolve a intensidade da chuva em mm/h (milimetros por hora), com 1 casa decimal.
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

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	// Menor chuva valida, em mm/h.
	@Override
	public double getFaixaMinima() {
		return FAIXA_MIN;
	}

	// Maior chuva valida, em mm/h.
	@Override
	public double getFaixaMaxima() {
		return FAIXA_MAX;
	}
}
