package br.com.fiap.territorio.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.territorio.dominio.Alerta;
import br.com.fiap.territorio.dominio.Medicao;
import br.com.fiap.territorio.dominio.NivelAlerta;
import br.com.fiap.territorio.dominio.PluviometroSensor;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import br.com.fiap.territorio.persistencia.AlertaRepository;
import br.com.fiap.territorio.persistencia.MedicaoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

	private static final String ID_SENSOR = "PLUV-01";
	private static final int LEITURAS_NECESSARIAS = 5;

	@Mock
	private MedicaoRepository medicaoRepository;

	@Mock
	private AlertaRepository alertaRepository;

	@InjectMocks
	private ServicoMonitoramento servico;

	private final PluviometroSensor sensor = new PluviometroSensor(ID_SENSOR);

	@Test
	void deveGerarAlertaCriticoQuandoPrevisaoAcimaDoLimiteCritico() {
		// Arrange
		when(medicaoRepository.findTop5BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(90.0, LEITURAS_NECESSARIAS));

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor);

		// Assert
		assertTrue(alerta.isPresent());
		assertEquals(NivelAlerta.CRITICO, alerta.get().getNivel());
		verify(alertaRepository).save(any(Alerta.class));
	}

	@Test
	void deveGerarAlertaDeAtencaoQuandoPrevisaoNaFaixaDeAtencao() {
		// Arrange
		when(medicaoRepository.findTop5BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(30.0, LEITURAS_NECESSARIAS));

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor);

		// Assert
		assertTrue(alerta.isPresent());
		assertEquals(NivelAlerta.ATENCAO, alerta.get().getNivel());
		verify(alertaRepository).save(any(Alerta.class));
	}

	@Test
	void deveNaoGerarAlertaQuandoPrevisaoAbaixoDoLimiteDeAtencao() {
		// Arrange
		when(medicaoRepository.findTop5BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(10.0, LEITURAS_NECESSARIAS));

		// Act
		Optional<Alerta> alerta = servico.analisar(sensor);

		// Assert
		assertTrue(alerta.isEmpty());
		verify(alertaRepository, never()).save(any());
	}

	@Test
	void deveLancarDadosInvalidosExceptionQuandoHistoricoInsuficiente() {
		// Arrange
		when(medicaoRepository.findTop5BySensorIdOrderByDataHoraDesc(ID_SENSOR))
				.thenReturn(criarHistorico(90.0, 3));

		// Act
		DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
				() -> servico.analisar(sensor));

		// Assert
		assertEquals("Historico insuficiente: 3 leituras, minimo 5", erro.getMessage());
		verify(alertaRepository, never()).save(any());
	}

	private List<Medicao> criarHistorico(double valor, int quantidade) {
		List<Medicao> historico = new ArrayList<>();
		for (int i = 0; i < quantidade; i++) {
			historico.add(new Medicao(sensor, valor));
		}
		return historico;
	}
}
