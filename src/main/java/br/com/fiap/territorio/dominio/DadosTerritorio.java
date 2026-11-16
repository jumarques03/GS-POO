package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import java.util.HashMap;
import java.util.Map;

public class DadosTerritorio {

	private int populacaoExposta;
	private final Map<String, Limiares> limiaresPorSensor = new HashMap<>();

	public DadosTerritorio(int populacaoExposta) {
		setPopulacaoExposta(populacaoExposta);
	}

	public int getPopulacaoExposta() {
		return populacaoExposta;
	}

	public void setPopulacaoExposta(int populacaoExposta) {
		if (populacaoExposta < 0) {
			throw new DadosInvalidosException("Populacao exposta invalida: " + populacaoExposta
					+ " (nao pode ser negativa)");
		}
		this.populacaoExposta = populacaoExposta;
	}

	public void definirLimiares(String tipoSensor, double atencao, double alerta, double critico) {
		limiaresPorSensor.put(tipoSensor, new Limiares(atencao, alerta, critico));
	}

	public Limiares getLimiares(String tipoSensor) {
		Limiares limiares = limiaresPorSensor.get(tipoSensor);
		if (limiares == null) {
			throw new DadosInvalidosException("Sem limiares definidos para o sensor: " + tipoSensor);
		}
		return limiares;
	}
}
