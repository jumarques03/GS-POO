package br.com.fiap.territorio.dominio;

public interface Sensor {

	String getId();

	String getTipo();

	// A unidade depende do tipo do sensor.
	double lerValor();

	boolean verificarFuncionamento();

	void setAtivo(boolean ativo);

	// Faixa valida de lerValor(), na mesma unidade dele.
	double getFaixaMinima();

	double getFaixaMaxima();
}
