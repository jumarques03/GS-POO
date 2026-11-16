package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.DadosInvalidosException;

public class Limiares {

	private final double atencao;
	private final double alerta;
	private final double critico;

	// Valores na unidade do sensor. A condicao e escrita em positivo e negada para tambem rejeitar NaN.
	public Limiares(double atencao, double alerta, double critico) {
		if (!(atencao > 0 && atencao < alerta && alerta < critico)) {
			throw new DadosInvalidosException("Limiares invalidos: atencao=" + atencao + ", alerta=" + alerta
					+ ", critico=" + critico + " (exigido: 0 < atencao < alerta < critico)");
		}
		this.atencao = atencao;
		this.alerta = alerta;
		this.critico = critico;
	}

	public double getAtencao() {
		return atencao;
	}

	public double getAlerta() {
		return alerta;
	}

	public double getCritico() {
		return critico;
	}
}
