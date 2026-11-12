package br.com.fiap.territorio.persistencia;

import br.com.fiap.territorio.dominio.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
}
