package br.com.logique.controller;

import java.io.File;
import java.io.Serializable;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.view.Results;
import br.com.logique.controller.email.Assuntos;
import br.com.logique.controller.email.EmailContainer;
import br.com.logique.outros.Files;

@Controller
@Path("/email")
public class EmailController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Result result;
	private EmailContainer emailContainer;
	private Environment ambiente;
	private final Files fileUpload;
	
	Logger log = Logger.getLogger(EmailController.class);
	

	public EmailController() {
		this(null, null, null, null);
	}

	@Inject
	public EmailController(Result result, EmailContainer emailContainer, Environment ambiente, Files fileUpload) {
		this.result = result;
		this.emailContainer = emailContainer;
		this.ambiente = ambiente;
		this.fileUpload = fileUpload;
	}

	@Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
	@Post("/enviar")
	public void enviar(String assunto, String destinatario, String comCopia, String conteudo) {
		try {
			enviarEmail(assunto, destinatario, comCopia, conteudo);
		} catch (EmailException e) {
			falhaEnvio(assunto, comCopia, conteudo, destinatario, e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
	@Post("/generico")
	public void enviarEmailGenerico(String assunto, String destinatario, String comCopia, String conteudo) {
		try {
			enviarEmail(assunto, destinatario, comCopia, conteudo);
		} catch (EmailException e) {
			falhaEnvio(assunto, comCopia, conteudo, destinatario, e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
	@Post("/emergencia-falta-gas")
	public void enviarEmailEmergenciaFaltaGas(String emailCliente, String conteudo) {
		String destinatario = ambiente.get("vraptor.simplemail.main.from");
		try {
			enviarEmail(Assuntos.EMERGENCIA_FALTA_GAS, destinatario, emailCliente, conteudo);

		} catch (EmailException e) {
			falhaEnvio(Assuntos.EMERGENCIA_FALTA_GAS, emailCliente, conteudo, destinatario, e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Consumes({ "application/json", "application/xml", "application/x-www-form-urlencoded" })
	@Post("/enviar-com-anexo")
	public void enviar(String assunto, String destinatario, String comCopia, String conteudo, UploadedFile anexo) {
		try {
			
			File anexoFile = fileUpload.save(anexo);
			String caminhoArquivo = fileUpload.getPath();
			String nomeArquivo = fileUpload.getNameFile();
			
			enviarEmailComAnexo(assunto, destinatario, comCopia, conteudo, anexoFile);
		} catch (EmailException e) {
			falhaEnvio(assunto, comCopia, conteudo, destinatario, e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void enviarEmail(String assunto, String destinatario, String comCopia, String conteudo)
			throws EmailException, MessagingException {
		validarTodosOsCampos(assunto, destinatario, conteudo);

		Email email = this.emailContainer.montarEmail(assunto, destinatario.trim(), conteudo, null);
		
		adicionarCopia(email, comCopia);
		this.emailContainer.enviarEmail(email);
		sucessoEnvio(assunto, comCopia, conteudo, destinatario);
	}
	
	private void enviarEmailComAnexo(String assunto, String destinatario, String comCopia, String conteudo, File anexo)
			throws EmailException, MessagingException {
		validarTodosOsCampos(assunto, destinatario, conteudo);

		Email email = this.emailContainer.montarEmail(assunto, destinatario.trim(), conteudo, anexo);
		
		adicionarCopia(email, comCopia);
		this.emailContainer.enviarEmail(email);
		sucessoEnvio(assunto, comCopia, conteudo, destinatario);
	}

	private void validarTodosOsCampos(String assunto, String destinatario, String conteudo) throws EmailException {
		validarConteudoEmail(conteudo);
		validarAssuntoEmail(assunto);
		validarDestinatarioEmail(destinatario);
	}

	private void adicionarCopia(Email email, String comCopia) throws EmailException {
		if (comCopia != null && !comCopia.isEmpty()) {
			email.addCc(comCopia.trim());
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