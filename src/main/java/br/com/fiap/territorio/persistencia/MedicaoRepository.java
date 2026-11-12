package br.com.fiap.territorio.persistencia;

import br.com.fiap.territorio.dominio.Medicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

	List<Medicao> findBySensorIdOrderByDataHoraAsc(String sensorId);

	// O 5 do nome deve acompanhar EstrategiaPrevisao.MINIMO_LEITURAS: o Spring nao aceita constante aqui.
	List<Medicao> findTop5BySensorIdOrderByDataHoraDesc(String sensorId);
}
