package br.com.fiap.territorio.dominio;

import br.com.fiap.territorio.SistemaMonitoramento;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import br.com.fiap.territorio.servico.ServicoMonitoramento;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class DemoTemporario {

	private static final String ID_SENSOR = "PLUV-01";
	private static final String ID_SENSOR_SEM_HISTORICO = "PLUV-02";
	private static final int QUANTIDADE_LEITURAS = 6;

	public static void main(String[] args) {
		ConfigurableApplicationContext contexto = SpringApplication.run(SistemaMonitoramento.class, args);
		ServicoMonitoramento servico = contexto.getBean(ServicoMonitoramento.class);
		PluviometroSensor sensor = new PluviometroSensor(ID_SENSOR);

		for (int i = 0; i < QUANTIDADE_LEITURAS; i++) {
			executar(() -> imprimirMedicao(servico.registrarLeitura(sensor)));
		}
		executar(() -> imprimirAnalise(servico.analisar(sensor)));

		executar(() -> imprimirAnalise(servico.analisar(new PluviometroSensor(ID_SENSOR_SEM_HISTORICO))));

		sensor.setAtivo(false);
		executar(() -> imprimirMedicao(servico.registrarLeitura(sensor)));

		System.out.println("Alertas emitidos:");
		for (Alerta alerta : servico.listarAlertas()) {
			System.out.println("#" + alerta.getId() + " | " + alerta.getNivel() + " | " + alerta.getMensagem());
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

	private static void imprimirMedicao(Medicao medicao) {
		System.out.println("Leitura #" + medicao.getId() + " | " + medicao.getSensorId() + " | "
				+ medicao.getValor() + " mm/h | " + medicao.getDataHora());
	}

	private static void imprimirAnalise(Optional<Alerta> alerta) {
		if (alerta.isPresent()) {
			System.out.println("Analise: " + alerta.get().getNivel() + " - " + alerta.get().getMensagem());
		} else {
			System.out.println("Analise: sem alerta");
		}
	}
}
