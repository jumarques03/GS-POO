package br.com.fiap.territorio.dominio;

import java.util.List;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

	private static final double ESCALA_UMA_CASA = 10.0;

	public PrevisaoMediaMovel() {
		super("Media movel");
	}

	@Override
	public double prever(List<Medicao> historico) {
		validarHistorico(historico);
		double soma = 0.0;
		for (Medicao medicao : historico) {
			soma += medicao.getValor();
		}
		double media = soma / historico.size();
		// Mesmo arredondamento do sensor: evita mensagens de alerta como "53.760000000000005".
		return Math.round(media * ESCALA_UMA_CASA) / ESCALA_UMA_CASA;
	}
}
