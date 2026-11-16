package br.com.fiap.territorio.servico;

import br.com.fiap.territorio.dominio.Alerta;
import br.com.fiap.territorio.dominio.DadosTerritorio;
import br.com.fiap.territorio.dominio.EstrategiaPrevisao;
import br.com.fiap.territorio.dominio.Limiares;
import br.com.fiap.territorio.dominio.Medicao;
import br.com.fiap.territorio.dominio.NivelAlerta;
import br.com.fiap.territorio.dominio.Sensor;
import br.com.fiap.territorio.persistencia.AlertaRepository;
import br.com.fiap.territorio.persistencia.MedicaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ServicoMonitoramento {

	private final MedicaoRepository medicaoRepository;
	private final AlertaRepository alertaRepository;

	public ServicoMonitoramento(MedicaoRepository medicaoRepository, AlertaRepository alertaRepository) {
		this.medicaoRepository = medicaoRepository;
		this.alertaRepository = alertaRepository;
	}

	public Medicao registrarLeitura(Sensor sensor) {
		Medicao medicao = new Medicao(sensor, sensor.lerValor());
		medicaoRepository.save(medicao);
		return medicao;
	}

	public Optional<Alerta> analisar(Sensor sensor, String tipoPrevisao, DadosTerritorio dados) {
		EstrategiaPrevisao estrategia = FabricaPrevisao.criar(tipoPrevisao);
		List<Medicao> historico = medicaoRepository.findTop10BySensorIdOrderByDataHoraDesc(sensor.getId());
		double previsao = estrategia.prever(historico);
		Limiares limiares = dados.getLimiares(sensor.getTipo());
		if (previsao >= limiares.getCritico()) {
			return Optional.of(gravarAlerta(sensor, NivelAlerta.CRITICO, previsao, limiares.getCritico()));
		}
		if (previsao >= limiares.getAlerta()) {
			return Optional.of(gravarAlerta(sensor, NivelAlerta.ALERTA, previsao, limiares.getAlerta()));
		}
		if (previsao >= limiares.getAtencao()) {
			return Optional.of(gravarAlerta(sensor, NivelAlerta.ATENCAO, previsao, limiares.getAtencao()));
		}
		return Optional.empty();
	}

	public List<Medicao> listarHistorico(String sensorId) {
		return medicaoRepository.findBySensorIdOrderByDataHoraAsc(sensorId);
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
