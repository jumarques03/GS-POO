package br.com.fiap.territorio.excecao;

public class DadosInvalidosException extends RuntimeException {

	public DadosInvalidosException(String mensagem) {
		super(mensagem);
	}
}
