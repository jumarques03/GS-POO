package br.com.fiap.territorio.dominio;

public abstract class ComponenteTerritorial {

	private static final String STATUS_INICIAL = "NAO VERIFICADO";

	private final String id;
	private final String nome;
	private final String regiao;
	private String status = STATUS_INICIAL;

	protected ComponenteTerritorial(String id, String nome, String regiao) {
		this.id = id;
		this.nome = nome;
		this.regiao = regiao;
	}

	public String getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getRegiao() {
		return regiao;
	}

	public String getStatus() {
		return status;
	}

	protected void setStatus(String status) {
		this.status = status;
	}

	public abstract String verificarStatus();
}
