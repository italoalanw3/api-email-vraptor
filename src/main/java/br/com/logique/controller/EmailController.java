package br.com.logique.controller;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.view.Results;
import br.com.logique.controller.email.Assuntos;
import br.com.logique.controller.email.EmailContainer;

@Controller
@Path("/email")
public class EmailController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Result result;
	private EmailContainer emailContainer;
	private Environment ambiente;
	Logger log = Logger.getLogger(EmailController.class);

	public EmailController() {
		this(null, null, null);
	}

	@Inject
	public EmailController(Result result, EmailContainer emailContainer, Environment ambiente) {
		this.result = result;
		this.emailContainer = emailContainer;
		this.ambiente = ambiente;
	}

	@Post("/generico")
	public void enviarEmailGenerico(String assunto, String destinatario, String comCopia, String conteudo) {
		try {
			enviarEmail(assunto, destinatario, comCopia, conteudo);
		} catch (EmailException e) {
			falhaEnvio(assunto, comCopia, conteudo, destinatario, e);
		}
	}

	@Post("/emergencia-falta-gas")
	public void enviarEmailEmergenciaFaltaGas(String emailCliente, String conteudo) {
		String destinatario = ambiente.get("vraptor.simplemail.main.from");
		try {
			enviarEmail(Assuntos.EMERGENCIA_FALTA_GAS, destinatario, emailCliente, conteudo);

		} catch (EmailException e) {
			falhaEnvio(Assuntos.EMERGENCIA_FALTA_GAS, emailCliente, conteudo, destinatario, e);
		}
	}

	private void enviarEmail(String assunto, String destinatario, String comCopia, String conteudo)
			throws EmailException {
		validarConteudoEmail(conteudo);
		validarAssuntoEmail(assunto);
		validarDestinatarioEmail(destinatario);

		Email email = this.emailContainer.montarEmail(assunto, destinatario, conteudo);
		adicionarCopia(email, comCopia);

		this.emailContainer.enviarEmail(email);
		sucessoEnvio(assunto, comCopia, conteudo, destinatario);
	}

	private void adicionarCopia(Email email, String comCopia) throws EmailException {
		if (comCopia != null) {
			email.addCc(comCopia);
		}
	}

	private void validarConteudoEmail(String conteudo) throws EmailException {
		if (conteudo == null || conteudo.isEmpty()) {
			throw new EmailException("conteúdo do e-mail não foi preenchido ou foi passando de forma errada");
		}
	}
	
	private void validarDestinatarioEmail(String destinatario) throws EmailException {
		if (destinatario == null || destinatario.isEmpty()) {
			throw new EmailException("destinatário do e-mail não foi preenchido ou foi passando de forma errada");
		}
	}

	private void validarAssuntoEmail(String assunto) throws EmailException {
		if (assunto == null || assunto.isEmpty()) {
			throw new EmailException("assunto do e-mail não foi preenchido ou foi passando de forma errada");
		}
	}

	private void falhaEnvio(String assunto, String comCopia, String conteudo, String destinatario,
			EmailException erro) {
		erro.printStackTrace();
		logarFalhaEnvio(assunto, destinatario, comCopia, conteudo, erro.getMessage());
		result.use(Results.http()).body("Falha no envio do e-mail: " + erro.getMessage()).setStatusCode(500);
	}

	private void sucessoEnvio(String assunto, String emailCliente, String conteudo, String destinatario) {
		logarEnvio(assunto, destinatario, emailCliente, conteudo);
		result.use(Results.json()).withoutRoot().from("Sucesso no envio do e-mail").serialize();
	}

	private void logarEnvio(String assunto, String destinatario, String comCopia, String conteudo) {
		log.info("Sucesso no envio do e-mail para o destinatário: " + destinatario);
		log.info("--- DETALHES ---");
		log.info("Assunto: " + assunto);
		log.info("Cópia: " + comCopia);
		log.info("Contéudo: " + conteudo);
	}

	private void logarFalhaEnvio(String assunto, String destinatario, String comCopia, String conteudo, String erro) {
		log.error("FALHA no envio do e-mail para o destinatário: " + destinatario);
		log.error("--- DETALHES ---");
		log.error("Assunto: " + assunto);
		log.error("Cópia: " + comCopia);
		log.error("Contéudo: " + conteudo);
		log.error("ERRO: " + erro);
	}

}