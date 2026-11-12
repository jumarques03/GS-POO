package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import java.util.List;

public abstract class EstrategiaPrevisao {

	protected static final int MINIMO_LEITURAS = 5;

	private final String nome;

	protected EstrategiaPrevisao(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	protected void validarHistorico(List<Medicao> historico) {
		if (historico.size() < MINIMO_LEITURAS) {
			throw new DadosInvalidosException("Historico insuficiente: " + historico.size()
					+ " leituras, minimo " + MINIMO_LEITURAS);
		}
	}

	// Devolve o valor previsto na mesma unidade das medicoes (pluviometro: mm/h).
	public abstract double prever(List<Medicao> historico);
}
