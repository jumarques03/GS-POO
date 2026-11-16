package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import java.util.List;

public abstract class EstrategiaPrevisao {

	private static final double ESCALA_UMA_CASA = 10.0;

	private final String nome;
	private final int minimoLeituras;

	protected EstrategiaPrevisao(String nome, int minimoLeituras) {
		this.nome = nome;
		this.minimoLeituras = minimoLeituras;
	}

	public String getNome() {
		return nome;
	}

	public int getMinimoLeituras() {
		return minimoLeituras;
	}

	protected void validarHistorico(List<Medicao> historico) {
		if (historico.size() < minimoLeituras) {
			throw new DadosInvalidosException("Historico insuficiente: " + historico.size()
					+ " leituras, minimo " + minimoLeituras);
		}
	}

	protected double media(List<Medicao> medicoes) {
		double soma = 0.0;
		for (Medicao medicao : medicoes) {
			soma += medicao.getValor();
		}
		return arredondar(soma / medicoes.size());
	}

	// Evita ruido de ponto flutuante nas mensagens de alerta (ex.: 53.760000000000005).
	protected double arredondar(double valor) {
		return Math.round(valor * ESCALA_UMA_CASA) / ESCALA_UMA_CASA;
	}

	// Devolve o valor previsto na mesma unidade das medicoes (pluviometro: mm/h).
	public abstract double prever(List<Medicao> historico);
}
