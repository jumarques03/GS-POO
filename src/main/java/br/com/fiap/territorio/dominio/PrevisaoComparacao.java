package br.com.fiap.territorio.dominio;

import java.util.List;

public class PrevisaoComparacao extends EstrategiaPrevisao {

	private static final int JANELA = 5;
	private static final int LEITURAS_NECESSARIAS = 2 * JANELA;

	public PrevisaoComparacao() {
		super("Comparacao com periodo anterior", LEITURAS_NECESSARIAS);
	}

	@Override
	public double prever(List<Medicao> historico) {
		validarHistorico(historico);
		double recente = media(historico.subList(0, JANELA));
		double anterior = media(historico.subList(JANELA, LEITURAS_NECESSARIAS));
		return arredondar(Math.max(0.0, recente + (recente - anterior)));
	}
}
