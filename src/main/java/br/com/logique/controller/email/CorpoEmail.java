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
	private String latitude;
	private String longitude;
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

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
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
		inserirCampoHtml(retorno, "Endere�o", getEndereco());
		inserirCampoHtml(retorno, "E-mail", getEmail());
		inserirCampoHtml(retorno, "N�mero do telefone do cliente", getNumeroTelefoneCliente());
		inserirCampoHtml(retorno, "N�mero do telefone usado", getNumeroTelefoneUsado());

		if (getInfo(getLatitude()) != "-" || getInfo(getLongitude()) != "-") {
			retorno.append("<p class='paragrafo'><b>");
			retorno.append("Localiza��o do dispositivo: </b><br>");
			retorno.append("&nbsp;&nbsp; latitude: " + getLatitude());
			retorno.append("<br>");
			retorno.append("&nbsp;&nbsp; longitude: " + getLongitude());
			retorno.append("<br>");
			retorno.append("&nbsp;&nbsp; Ver localiza��o: <a href='http://maps.google.com/?q=" + getLatitude() + ","
					+ getLongitude() + "' >abrir mapa</a>");
			retorno.append("</p>");
		} else {
			inserirCampoHtml(retorno, "Localiza��o do dispositivo", "n�o foi poss�vel obter localiza��o");
		}

		inserirCampoHtml(retorno, "Informa��o escrita pelo usu�rio",
				"<br>" + getInfo(getInformacaoEscritaPeloUsuario()));

		inserirCampoHtml(retorno, "Origem", getOrigem());

		return retorno.toString();
	}

	private void inserirCampoHtml(StringBuilder retorno, String label, String valor) {
		retorno.append("<p class='paragrafo'><b>");
		retorno.append(label);
		retorno.append(": </b>");
		retorno.append(getInfo(valor));
		retorno.append("</p>");
	}

	private String getInfo(String info) {
		if (info == null || info.isEmpty()) {
			return "-";
		} else {
			return info;
		}
	}

}
