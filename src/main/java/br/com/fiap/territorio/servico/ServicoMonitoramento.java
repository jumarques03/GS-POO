package br.com.fiap.territorio.servico;

import br.com.fiap.territorio.dominio.Alerta;
import br.com.fiap.territorio.dominio.EstrategiaPrevisao;
import br.com.fiap.territorio.dominio.Medicao;
import br.com.fiap.territorio.dominio.NivelAlerta;
import br.com.fiap.territorio.dominio.PrevisaoMediaMovel;
import br.com.fiap.territorio.dominio.Sensor;
import br.com.fiap.territorio.persistencia.AlertaRepository;
import br.com.fiap.territorio.persistencia.MedicaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ServicoMonitoramento {

	// TODO etapa 5: mover para DadosTerritorio com setters validando (limiares em mm/h).
	private static final double LIMIAR_ATENCAO = 20.0;
	private static final double LIMIAR_ALERTA = 50.0;
	private static final double LIMIAR_CRITICO = 80.0;

	private final MedicaoRepository medicaoRepository;
	private final AlertaRepository alertaRepository;
	// TODO etapa 5: obter pela FabricaPrevisao.
	private final EstrategiaPrevisao estrategia = new PrevisaoMediaMovel();

	public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository) {
		this.medicaoRepository = medicaoRepository;
		this.alertaRepository = alertaRepository;
	}

	public Medicao registrarLeitura(Sensor sensor) {
		Medicao medicao = new Medicao(sensor, sensor.lerValor());
		medicaoRepository.save(medicao);
		return medicao;
	}

	public Optional<Alerta> analisar(Sensor sensor) {
		List<Medicao> historico = medicaoRepository.findTop5BySensorIdOrderByDataHoraDesc(sensor.getId());
		double previsao = estrategia.prever(historico);
		if (previsao >= LIMIAR_CRITICO) {
			return Optional.of(gravarAlerta(sensor, NivelAlerta.CRITICO, previsao, LIMIAR_CRITICO));
		}
		if (previsao >= LIMIAR_ALERTA) {
			return Optional.of(gravarAlerta(sensor, NivelAlerta.ALERTA, previsao, LIMIAR_ALERTA));
		}
		if (previsao >= LIMIAR_ATENCAO) {
			return Optional.of(gravarAlerta(sensor, NivelAlerta.ATENCAO, previsao, LIMIAR_ATENCAO));
		}
		return Optional.empty();
	}

	public List<Alerta> listarAlertas() {
		return alertaRepository.findAll();
	}

	private Alerta gravarAlerta(Sensor sensor, NivelAlerta nivel, double previsao, double limite) {
		Alerta alerta = new Alerta(sensor.getId(), nivel, previsao, limite);
		alertaRepository.save(alerta);
		return alerta;
	}
}
