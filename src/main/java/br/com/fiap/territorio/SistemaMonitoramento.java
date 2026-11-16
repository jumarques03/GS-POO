package br.com.fiap.territorio;

import br.com.fiap.territorio.dominio.Alerta;
import br.com.fiap.territorio.dominio.DadosTerritorio;
import br.com.fiap.territorio.dominio.Limiares;
import br.com.fiap.territorio.dominio.Medicao;
import br.com.fiap.territorio.dominio.NivelCorregoSensor;
import br.com.fiap.territorio.dominio.PluviometroSensor;
import br.com.fiap.territorio.dominio.PontoVulneravel;
import br.com.fiap.territorio.dominio.Sensor;
import br.com.fiap.territorio.dominio.UmidadeSoloSensor;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import br.com.fiap.territorio.servico.FabricaPrevisao;
import br.com.fiap.territorio.servico.ServicoMonitoramento;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal. Fica no pacote RAIZ para o Spring encontrar as classes
 * dos subpacotes (dominio, excecao, persistencia, servico).
 * Tambem e o menu de console: o Spring chama run() depois de subir.
 */
@SpringBootApplication
public class SistemaMonitoramento implements CommandLineRunner {

	private static final String MENU = """

			=== Territory Predictive Insights ===
			1 - Verificar sensores
			2 - Rodar previsoes
			3 - Ver historico
			4 - Gerenciar dados do territorio
			5 - Status completo
			0 - Sair
			""";
	private static final String OPCAO_INVALIDA = "Opcao invalida.";
	private static final int OPCAO_SAIR = 0;

	private static final int POPULACAO_INICIAL = 5000;
	// Limiares iniciais, na unidade de cada sensor: mm/h, cm e %.
	private static final double PLUVIOMETRO_ATENCAO = 20.0;
	private static final double PLUVIOMETRO_ALERTA = 50.0;
	private static final double PLUVIOMETRO_CRITICO = 80.0;
	private static final double CORREGO_ATENCAO = 150.0;
	private static final double CORREGO_ALERTA = 250.0;
	private static final double CORREGO_CRITICO = 320.0;
	private static final double UMIDADE_ATENCAO = 70.0;
	private static final double UMIDADE_ALERTA = 85.0;
	private static final double UMIDADE_CRITICO = 95.0;

	private static final Map<String, String> UNIDADES = Map.of(
			PluviometroSensor.TIPO, "mm/h",
			NivelCorregoSensor.TIPO, "cm",
			UmidadeSoloSensor.TIPO, "%");

	private final ServicoMonitoramento servico;
	private final List<Sensor> sensores = List.of(new PluviometroSensor("PLUV-01"),
			new NivelCorregoSensor("CORR-01"), new UmidadeSoloSensor("UMID-01"));
	private final DadosTerritorio dados = criarDados();
	private final PontoVulneravel ponto = new PontoVulneravel("PV-01", "Passagem subterranea", "Zona Sul", sensores);

	public SistemaMonitoramento(ServicoMonitoramento servico) {
		this.servico = servico;
	}

	public static void main(String[] args) {
		SpringApplication.run(SistemaMonitoramento.class, args);
	}

	@Override
	public void run(String... args) {
		try (Scanner entrada = new Scanner(System.in)) {
			boolean continuar = true;
			while (continuar) {
				continuar = processarOpcao(entrada);
			}
		}
	}

	private static DadosTerritorio criarDados() {
		DadosTerritorio dados = new DadosTerritorio(POPULACAO_INICIAL);
		dados.definirLimiares(PluviometroSensor.TIPO, PLUVIOMETRO_ATENCAO, PLUVIOMETRO_ALERTA, PLUVIOMETRO_CRITICO);
		dados.definirLimiares(NivelCorregoSensor.TIPO, CORREGO_ATENCAO, CORREGO_ALERTA, CORREGO_CRITICO);
		dados.definirLimiares(UmidadeSoloSensor.TIPO, UMIDADE_ATENCAO, UMIDADE_ALERTA, UMIDADE_CRITICO);
		return dados;
	}

	private boolean processarOpcao(Scanner entrada) {
		System.out.print(MENU);
		try {
			int opcao = lerInteiro(entrada, "Opcao: ");
			if (opcao == OPCAO_SAIR) {
				return false;
			}
			executarOpcao(entrada, opcao);
		} catch (NumberFormatException e) {
			System.out.println("Entrada invalida: digite apenas numeros (decimais com ponto, ex.: 50.5).");
		} catch (DadosInvalidosException e) {
			System.out.println("Dado invalido, revise o valor informado: " + e.getMessage());
		}
		return true;
	}

	private void executarOpcao(Scanner entrada, int opcao) {
		switch (opcao) {
			case 1 -> verificarSensores();
			case 2 -> rodarPrevisoes(entrada);
			case 3 -> verHistorico(entrada);
			case 4 -> gerenciarDados(entrada);
			case 5 -> mostrarStatus();
			default -> System.out.println(OPCAO_INVALIDA);
		}
	}

	private void verificarSensores() {
		for (Sensor sensor : sensores) {
			try {
				Medicao medicao = servico.registrarLeitura(sensor);
				System.out.println(sensor.getId() + " | " + sensor.getTipo() + " | "
						+ medicao.getValor() + " " + unidadeDe(sensor));
			} catch (FonteIndisponivelException e) {
				System.out.println("Falha no sensor, verifique o equipamento: " + e.getMessage());
			}
		}
	}

	private void rodarPrevisoes(Scanner entrada) {
		String tipoPrevisao = escolherEstrategia(entrada);
		for (Sensor sensor : sensores) {
			try {
				Optional<Alerta> alerta = servico.analisar(sensor, tipoPrevisao, dados);
				imprimirAnalise(sensor, alerta);
			} catch (DadosInvalidosException e) {
				System.out.println(sensor.getId() + ": " + e.getMessage());
			}
		}
	}

	private String escolherEstrategia(Scanner entrada) {
		System.out.println("1 - Media movel | 2 - Comparacao com periodo anterior");
		int opcao = lerInteiro(entrada, "Estrategia: ");
		return switch (opcao) {
			case 1 -> FabricaPrevisao.MEDIA_MOVEL;
			case 2 -> FabricaPrevisao.COMPARACAO;
			default -> throw new DadosInvalidosException("Estrategia invalida: " + opcao + " (use 1 ou 2)");
		};
	}

	private void imprimirAnalise(Sensor sensor, Optional<Alerta> alerta) {
		if (alerta.isPresent()) {
			System.out.println(sensor.getId() + ": " + alerta.get().getNivel() + " - " + alerta.get().getMensagem());
		} else {
			System.out.println(sensor.getId() + ": sem alerta");
		}
	}

	private void verHistorico(Scanner entrada) {
		System.out.println("1 - Medicoes de um sensor | 2 - Alertas emitidos");
		switch (lerInteiro(entrada, "Opcao: ")) {
			case 1 -> imprimirMedicoes(escolherSensor(entrada));
			case 2 -> imprimirAlertas();
			default -> System.out.println(OPCAO_INVALIDA);
		}
	}

	private void imprimirMedicoes(Sensor sensor) {
		List<Medicao> historico = servico.listarHistorico(sensor.getId());
		if (historico.isEmpty()) {
			System.out.println("Nenhuma medicao registrada para " + sensor.getId() + ".");
		}
		for (Medicao medicao : historico) {
			System.out.println(medicao.getDataHora() + " | " + medicao.getSensorId() + " | "
					+ medicao.getValor() + " " + unidadeDe(sensor));
		}
	}

	private void imprimirAlertas() {
		List<Alerta> alertas = servico.listarAlertas();
		if (alertas.isEmpty()) {
			System.out.println("Nenhum alerta emitido.");
		}
		for (Alerta alerta : alertas) {
			System.out.println(alerta.getDataHora() + " | " + alerta.getNivel() + " | " + alerta.getMensagem());
		}
	}

	private void gerenciarDados(Scanner entrada) {
		System.out.println("1 - Ver dados | 2 - Alterar populacao | 3 - Alterar limiares de um sensor"
				+ " | 4 - Ativar/desativar sensor (simular falha)");
		switch (lerInteiro(entrada, "Opcao: ")) {
			case 1 -> imprimirDados();
			case 2 -> alterarPopulacao(entrada);
			case 3 -> alterarLimiares(entrada);
			case 4 -> alternarSensor(entrada);
			default -> System.out.println(OPCAO_INVALIDA);
		}
	}

	private void alterarPopulacao(Scanner entrada) {
		dados.setPopulacaoExposta(lerInteiro(entrada, "Nova populacao exposta: "));
		System.out.println("Populacao exposta atualizada para " + dados.getPopulacaoExposta() + ".");
	}

	private void alterarLimiares(Scanner entrada) {
		Sensor sensor = escolherSensor(entrada);
		String unidade = unidadeDe(sensor);
		double atencao = lerDecimal(entrada, "Limiar de ATENCAO (" + unidade + "): ");
		double alerta = lerDecimal(entrada, "Limiar de ALERTA (" + unidade + "): ");
		double critico = lerDecimal(entrada, "Limiar CRITICO (" + unidade + "): ");
		dados.definirLimiares(sensor.getTipo(), atencao, alerta, critico);
		System.out.println("Limiares de " + sensor.getTipo() + " atualizados.");
	}

	private void alternarSensor(Scanner entrada) {
		Sensor sensor = escolherSensor(entrada);
		sensor.setAtivo(!sensor.verificarFuncionamento());
		String situacao = sensor.verificarFuncionamento() ? "ativo." : "inativo (simulacao de falha).";
		System.out.println(sensor.getId() + " agora esta " + situacao);
	}

	private void mostrarStatus() {
		System.out.println("Ponto vulneravel " + ponto.getId() + " - " + ponto.getNome()
				+ " (" + ponto.getRegiao() + "): " + ponto.verificarStatus());
		for (Sensor sensor : sensores) {
			System.out.println(sensor.getId() + " | " + sensor.getTipo() + " | "
					+ (sensor.verificarFuncionamento() ? "funcionando" : "com falha"));
		}
		imprimirDados();
		System.out.println("Total de alertas emitidos: " + servico.listarAlertas().size());
	}

	private void imprimirDados() {
		System.out.println("Populacao exposta: " + dados.getPopulacaoExposta());
		for (Sensor sensor : sensores) {
			Limiares limiares = dados.getLimiares(sensor.getTipo());
			System.out.println("Limiares de " + sensor.getTipo() + ": atencao " + limiares.getAtencao()
					+ " | alerta " + limiares.getAlerta() + " | critico " + limiares.getCritico()
					+ " " + unidadeDe(sensor));
		}
	}

	private Sensor escolherSensor(Scanner entrada) {
		for (int i = 0; i < sensores.size(); i++) {
			System.out.println((i + 1) + " - " + sensores.get(i).getId() + " (" + sensores.get(i).getTipo() + ")");
		}
		int numero = lerInteiro(entrada, "Sensor: ");
		if (numero < 1 || numero > sensores.size()) {
			throw new DadosInvalidosException("Sensor invalido: " + numero + " (escolha de 1 a " + sensores.size() + ")");
		}
		return sensores.get(numero - 1);
	}

	private String unidadeDe(Sensor sensor) {
		return UNIDADES.get(sensor.getTipo());
	}

	private int lerInteiro(Scanner entrada, String mensagem) {
		System.out.print(mensagem);
		return Integer.parseInt(entrada.nextLine().trim());
	}

	private double lerDecimal(Scanner entrada, String mensagem) {
		System.out.print(mensagem);
		return Double.parseDouble(entrada.nextLine().trim());
	}
}
