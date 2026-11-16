package br.com.fiap.territorio.dominio;

import java.util.List;

public class PontoVulneravel extends ComponenteTerritorial {

	private static final String STATUS_OPERANDO = "OPERANDO";
	private static final String STATUS_COM_FALHA = "COM FALHA";

	private final List<Sensor> sensores;

	public PontoVulneravel(String id, String nome, String regiao, List<Sensor> sensores) {
		super(id, nome, regiao);
		this.sensores = sensores;
	}

	@Override
	public String verificarStatus() {
		String novoStatus = STATUS_OPERANDO;
		for (Sensor sensor : sensores) {
			if (!sensor.verificarFuncionamento()) {
				novoStatus = STATUS_COM_FALHA;
			}
		}
		setStatus(novoStatus);
		return novoStatus;
	}
}
