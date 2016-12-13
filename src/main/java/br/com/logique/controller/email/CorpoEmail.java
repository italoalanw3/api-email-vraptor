package br.com.logique.controller.email;

import java.io.Serializable;

public class CorpoEmail implements Serializable {

	private static final long serialVersionUID = 1L;
	private String dataHora;
	private String nomeCliente;
	private String codigoCliente;
	private String endereco;
	private String email;
	private String numeroTelefoneCliente;
	private String numeroTelefoneUsado;
	private double latitude;
	private double longitude;
	private String informacaoEscritaPeloUsuario;
	private String origem;

	public CorpoEmail() {
	}

	public String getDataHora() {
		return dataHora;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public String getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumeroTelefoneCliente() {
		return numeroTelefoneCliente;
	}

	public void setNumeroTelefoneCliente(String numeroTelefoneCliente) {
		this.numeroTelefoneCliente = numeroTelefoneCliente;
	}

	public String getNumeroTelefoneUsado() {
		return numeroTelefoneUsado;
	}

	public void setNumeroTelefoneUsado(String numeroTelefoneUsado) {
		this.numeroTelefoneUsado = numeroTelefoneUsado;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getInformacaoEscritaPeloUsuario() {
		return informacaoEscritaPeloUsuario;
	}

	public void setInformacaoEscritaPeloUsuario(String informacaoEscritaPeloUsuario) {
		this.informacaoEscritaPeloUsuario = informacaoEscritaPeloUsuario;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	@Override
	public String toString() {
		StringBuilder retorno = new StringBuilder();

		retorno.append("<style>");
		retorno.append(".paragrafo {");
		retorno.append("font-family: arial,sans-serif;");
		retorno.append("}");
		retorno.append("</style>");

		inserirCampoHtml(retorno, "Data e hora do celular", getDataHora());
		inserirCampoHtml(retorno, "Cliente", getNomeCliente());
		inserirCampoHtml(retorno, "Endereço", getEndereco());
		inserirCampoHtml(retorno, "E-mail", getEmail());
		inserirCampoHtml(retorno, "Número do telefone do cliente", getNumeroTelefoneCliente());
		inserirCampoHtml(retorno, "Número do telefone usado", getNumeroTelefoneUsado());

		if (getLatitude() != 0 || getLongitude() != 0) {
			retorno.append("<p class='paragrafo'><b>");
			retorno.append("Localização do dispositivo: </b><br>");
			retorno.append("&nbsp;&nbsp; latitude: " + getLatitude());
			retorno.append("<br>");
			retorno.append("&nbsp;&nbsp; longitude: " + getLongitude());
			retorno.append("<br>");
			retorno.append("&nbsp;&nbsp; Ver localização: <a href='http://maps.google.com/?q=" + getLatitude() + ","
					+ getLongitude() + "' >abrir mapa</a>");
			retorno.append("</p>");
		} else {
			inserirCampoHtml(retorno, "Localização do dispositivo", "não foi possível obter localização");
		}

		inserirCampoHtml(retorno, "Informação escrita pelo usuário", getInformacaoEscritaPeloUsuario());

		inserirCampoHtml(retorno, "Origem", getOrigem());

		return retorno.toString();
	}

	private void inserirCampoHtml(StringBuilder retorno, String label, String valor) {
		retorno.append("<p class='paragrafo'><b>");
		retorno.append(label);
		retorno.append(": </b>");
		retorno.append(valor);
		retorno.append("</p>");
	}

}
