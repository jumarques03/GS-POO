package br.com.fiap.territorio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal. Fica no pacote RAIZ para o Spring encontrar as classes
 * dos subpacotes (dominio, excecao, persistencia, servico).
 * O menu de console entra na Etapa 6.
 */
@SpringBootApplication
public class SistemaMonitoramento {

	public static void main(String[] args) {
		SpringApplication.run(SistemaMonitoramento.class, args);
	}
}
