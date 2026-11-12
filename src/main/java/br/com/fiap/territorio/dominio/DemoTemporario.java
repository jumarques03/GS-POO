package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.excecao.DadosInvalidosException;
import br.com.fiap.territorio.excecao.FonteIndisponivelException;

public class DemoTemporario {

	private static final int QUANTIDADE_LEITURAS = 5;
	private static final double VALOR_FORA_DA_FAIXA = 200.0;

	public static void main(String[] args) {
		PluviometroSensor sensor = new PluviometroSensor("PLUV-01");

		for (int i = 0; i < QUANTIDADE_LEITURAS; i++) {
			executar(() -> imprimir(new Medicao(sensor, sensor.lerValor())));
		}

		executar(() -> imprimir(new Medicao(sensor, VALOR_FORA_DA_FAIXA)));

		sensor.setAtivo(false);
		executar(() -> imprimir(new Medicao(sensor, sensor.lerValor())));
	}

	private static void executar(Runnable acao) {
		try {
			acao.run();
		} catch (DadosInvalidosException e) {
			System.out.println("Dado invalido, revise o valor informado: " + e.getMessage());
		} catch (FonteIndisponivelException e) {
			System.out.println("Falha no sensor, verifique o equipamento: " + e.getMessage());
		}
	}

	private static void imprimir(Medicao medicao) {
		System.out.println(medicao.getSensorId() + " | " + medicao.getTipo() + " | "
				+ medicao.getValor() + " mm/h | " + medicao.getDataHora());
	}
}
