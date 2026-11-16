package br.com.fiap.territorio.servico;

import br.com.fiap.territorio.dominio.EstrategiaPrevisao;
import br.com.fiap.territorio.dominio.PrevisaoComparacao;
import br.com.fiap.territorio.dominio.PrevisaoMediaMovel;
import br.com.fiap.territorio.excecao.DadosInvalidosException;
import java.util.Map;
import java.util.function.Supplier;

public class FabricaPrevisao {

	public static final String MEDIA_MOVEL = "MEDIA_MOVEL";
	public static final String COMPARACAO = "COMPARACAO";

	private static final Map<String, Supplier<EstrategiaPrevisao>> ESTRATEGIAS = Map.of(
			MEDIA_MOVEL, PrevisaoMediaMovel::new,
			COMPARACAO, PrevisaoComparacao::new);

	public static EstrategiaPrevisao criar(String tipo) {
		Supplier<EstrategiaPrevisao> fabricar = ESTRATEGIAS.get(tipo);
		if (fabricar == null) {
			throw new DadosInvalidosException("Tipo de previsao desconhecido: " + tipo
					+ " (opcoes validas: " + MEDIA_MOVEL + ", " + COMPARACAO + ")");
		}
		return fabricar.get();
	}
}
