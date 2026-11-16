package br.com.fiap.territorio.persistencia;

import br.com.fiap.territorio.dominio.Medicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

	List<Medicao> findBySensorIdOrderByDataHoraAsc(String sensorId);

	// 10 = duas janelas de 5 leituras, o maior historico que uma estrategia usa (PrevisaoComparacao).
	List<Medicao> findTop10BySensorIdOrderByDataHoraDesc(String sensorId);
}
