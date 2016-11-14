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
	public EmailController(Result result, EmailContainer emailContainer,
			Environment ambiente) {
		this.result = result;
		this.emailContainer = emailContainer;
		this.ambiente = ambiente;
	}

	@Post("/generico")
	public void enviarEmail(String assunto, String destinatario,
			String comCopia, String conteudo) {
		try {
			Email email = this.emailContainer.montarEmail(assunto,
					destinatario, conteudo);
			if (comCopia != null) {
				email.addCc(comCopia);
			}
			this.emailContainer.enviarEmail(email);
			logarEnvio(assunto, destinatario, comCopia, conteudo);
			result.use(Results.json()).from("ok", "Sucesso no envio do e-mail");

		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.use(Results.http()).sendError(500,
					"Falha no envio do e-mail: " + e.getMessage());
		}
	}

	@Post("/emergencia-falta-gas")
	public void enviarEmail(String emailCliente, String conteudo) {
		try {
			String destinatario = ambiente.get("vraptor.simplemail.main.from");
			Email email = this.emailContainer.montarEmail(
					Assuntos.EMERGENCIA_FALTA_GAS, destinatario, conteudo);
			email.addCc(emailCliente);
			this.emailContainer.enviarEmail(email);
			logarEnvio(Assuntos.EMERGENCIA_FALTA_GAS, destinatario,
					emailCliente, conteudo);
			result.use(Results.json()).from("ok", "Sucesso no envio do e-mail");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void logarEnvio(String assunto, String destinatario,
			String comCopia, String conteudo) {
		log.info("Sucesso no envio do e-mail para o destinatário: "
				+ destinatario);
		log.info("--- DETALHES ---");
		log.info("Assunto: " + assunto);
		log.info("Cópia: " + comCopia);
		log.info("Contéudo: " + conteudo);
	}

}