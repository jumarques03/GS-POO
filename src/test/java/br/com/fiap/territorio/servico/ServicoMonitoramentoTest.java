package br.com.fiap.territorio.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.territorio.dominio.Alerta;
import br.com.fiap.territorio.dominio.DadosTerritorio;
import br.com.fiap.territorio.dominio.Medicao;
import br.com.fiap.territorio.dominio.NivelAlerta;
import br.com.fiap.territorio.dominio.PluviometroSensor;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import br.com.fiap.territorio.excecao.FonteIndisponivelException;
import br.com.fiap.territorio.persistencia.AlertaRepository;
import br.com.fiap.territorio.persistencia.MedicaoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

	private static final String ID_SENSOR = "PLUV-01";
	private static final int LEITURAS_NECESSARIAS = 5;
	private static final double LIMIAR_ATENCAO = 20.0;
	private static final double LIMIAR_ALERTA = 50.0;
	private static final double LIMIAR_CRITICO = 80.0;

	@Mock
	private MedicaoRepository medicaoRepository;

	@Mock
	private AlertaRepository alertaRepository;

	@InjectMocks
	private ServicoMonitoramento servico;

	private final PluviometroSensor sensor = new PluviometroSensor(ID_SENSOR);
	private final DadosTerritorio dados = new DadosTerritorio(5000);

	@BeforeEach
	void definirLimiares() {
		dados.definirLimiares(PluviometroSensor.TIPO, LIMIAR_ATENCAO, LIMIAR_ALERTA, LIMIAR_CRITICO);
	}

	@Test
	void deveGerarAlertaCriticoQuandoPrevisaoAcimaDoLimiteCritico() {
		// Arrange
		when(medicaoRepository.findTop10BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(90.0, LEITURAS_NECESSARIAS));

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor, FabricaPrevisao.MEDIA_MOVEL, dados);

		// Assert
		assertTrue(alerta.isPresent());
		assertEquals(NivelAlerta.CRITICO, alerta.get().getNivel());
		verify(alertaRepository).save(any(Alerta.class));
	}

	@Test
	void deveGerarAlertaDeAtencaoQuandoPrevisaoNaFaixaDeAtencao() {
		// Arrange
		when(medicaoRepository.findTop10BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(30.0, LEITURAS_NECESSARIAS));

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor, FabricaPrevisao.MEDIA_MOVEL, dados);

		// Assert
		assertTrue(alerta.isPresent());
		assertEquals(NivelAlerta.ATENCAO, alerta.get().getNivel());
		verify(alertaRepository).save(any(Alerta.class));
	}

	@Test
	void deveNaoGerarAlertaQuandoPrevisaoAbaixoDoLimiteDeAtencao() {
		// Arrange
		when(medicaoRepository.findTop10BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(10.0, LEITURAS_NECESSARIAS));

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor, FabricaPrevisao.MEDIA_MOVEL, dados);

		// Assert
		assertTrue(alerta.isEmpty());
		verify(alertaRepository, never()).save(any());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoHistoricoInsuficiente() {
		// Arrange
		when(medicaoRepository.findTop10BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(90.0, 3));

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> servico.analisar(sensor, FabricaPrevisao.MEDIA_MOVEL, dados));

		// Assert
		assertEquals("Historico insuficiente: 3 leituras, minimo 5", erro.getMessage());
		verify(alertaRepository, never()).save(any());
	}

	// A media das 10 leituras (50.0) daria ALERTA; a tendencia (60 + (60 - 40) = 80.0) alcanca CRITICO.
	@Test
	void deveUsarEstrategiaDeComparacao() {
		// Arrange
		List<Medicao> historico = criarHistorico(60.0, LEITURAS_NECESSARIAS);
		historico.addAll(criarHistorico(40.0, LEITURAS_NECESSARIAS));
		when(medicaoRepository.findTop10BySensorIdOrderByDataHoraDesc(ID_SENSOR)).thenReturn(historico);

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor, FabricaPrevisao.COMPARACAO, dados);

		// Assert
		assertTrue(alerta.isPresent());
		assertEquals(NivelAlerta.CRITICO, alerta.get().getNivel());
		assertEquals(80.0, alerta.get().getValorPrevisto());
	}

	@Test
	void deveSalvarMedicaoQuandoSensorFunciona() {
		// Arrange
		sensor.setAtivo(true);

		// Act
		Medicao medicao = servico.registrarLeitura(sensor);

		// Assert
		verify(medicaoRepository).save(any(Medicao.class));
		assertTrue(medicao.getValor() >= sensor.getFaixaMinima() && medicao.getValor() <= sensor.getFaixaMaxima());
	}

	@Test
	void deveNaoSalvarMedicaoQuandoSensorInativo() {
		// Arrange
		sensor.setAtivo(false);

		// Act
		FonteIndisponivelException erro = assertThrows(FonteIndisponivelException.class,
				() -> servico.registrarLeitura(sensor));

		// Assert
		assertEquals("Sensor indisponivel: PLUV-01 (PLUVIOMETRO) esta inativo ou com falha", erro.getMessage());
		verify(medicaoRepository, never()).save(any());
	}

	private List<Medicao> criarHistorico(double valor, int quantidade) {
		List<Medicao> historico = new ArrayList<>();
		for (int i = 0; i < quantidade; i++) {
			historico.add(new Medicao(sensor, valor));
		}
		return historico;
	}
}
