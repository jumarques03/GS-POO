package br.com.fiap.territorio.persistencia;

import br.com.fiap.territorio.dominio.Medicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

	List<Medicao> findBySensorIdOrderByDataHoraAsc(String sensorId);
}
