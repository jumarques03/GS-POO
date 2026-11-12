package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.SistemaMonitoramento;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import br.com.fiap.territorio.persistencia.MedicaoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class DemoTemporario {

	private static final String ID_SENSOR = "PLUV-01";
	private static final int QUANTIDADE_LEITURAS = 3;
	private static final double VALOR_FORA_DA_FAIXA = 200.0;

	public static void main(String[] args) {
		ConfigurableApplicationContext contexto = SpringApplication.run(SistemaMonitoramento.class, args);
		MedicaoRepository repositorio = contexto.getBean(MedicaoRepository.class);
		PluviometroSensor sensor = new PluviometroSensor(ID_SENSOR);

		for (int i = 0; i < QUANTIDADE_LEITURAS; i++) {
			executar(() -> repositorio.save(new Medicao(sensor, sensor.lerValor())));
		}

		executar(() -> repositorio.save(new Medicao(sensor, VALOR_FORA_DA_FAIXA)));

		sensor.setAtivo(false);
		executar(() -> repositorio.save(new Medicao(sensor, sensor.lerValor())));

		System.out.println("Historico de " + ID_SENSOR + ":");
		for (Medicao medicao : repositorio.findBySensorIdOrderByDataHoraAsc(ID_SENSOR)) {
			imprimir(medicao);
		}
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
		System.out.println("#" + medicao.getId() + " | " + medicao.getSensorId() + " | " + medicao.getTipo() + " | "
				+ medicao.getValor() + " mm/h | " + medicao.getDataHora());
	}
}
