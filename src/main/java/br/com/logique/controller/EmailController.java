package br.com.logique.controller;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import br.com.caelum.vraptor.observer.upload.UploadSizeLimit;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.serialization.gson.WithoutRoot;
import br.com.caelum.vraptor.view.Results;
import br.com.logique.controller.email.CorpoEmail;
import br.com.logique.controller.email.EmailContainer;
import br.com.logique.outros.Files;

@Controller
@Path("/email")
public class EmailController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Result result;
	private EmailContainer emailContainer;
	private final Files fileUpload;

	Logger log = Logger.getLogger(EmailController.class);

	public EmailController() {
		this(null, null, null);
	}

	@Inject
	public EmailController(Result result, EmailContainer emailContainer, Files fileUpload) {
		this.result = result;
		this.emailContainer = emailContainer;
		this.fileUpload = fileUpload;
	}

	@Consumes(value = { "application/json", "application/xml",
			"application/x-www-form-urlencoded" }, options = WithoutRoot.class)
	@Post("/enviar")
	public void enviar(String assunto, String destinatario, String comCopia, CorpoEmail corpoEmail) {
		try {
			enviarEmail(assunto, destinatario, comCopia, corpoEmail);
		} catch (EmailException e) {
			falhaEnvio(assunto, comCopia, corpoEmail, destinatario, e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private File criarArquivoAnexo(UploadedFile anexo) {
		File anexoFile = null;
		if (anexo != null) {
			anexoFile = fileUpload.save(anexo);
		}
		return anexoFile;
	}

	@UploadSizeLimit(sizeLimit = 536870912, fileSizeLimit = 104857600)
	@Post("/enviar-com-multi-anexos")
	public void enviar(String assunto, String destinatario, String comCopia, CorpoEmail corpoEmail,
			List<UploadedFile> anexos) {
		try {

			List<File> anexosFiles = new ArrayList<File>();
			if (anexos != null) {
				for (UploadedFile uploadedFile : anexos) {
					anexosFiles.add(criarArquivoAnexo(uploadedFile));
				}
			}

			enviarEmailComMultiAnexo(assunto, destinatario, comCopia, corpoEmail, anexosFiles);
		} catch (EmailException e) {
			falhaEnvio(assunto, comCopia, corpoEmail, destinatario, e);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void enviarEmail(String assunto, String destinatario, String comCopia, CorpoEmail corpoEmail)
			throws EmailException, MessagingException {
		validarTodosOsCampos(assunto, destinatario, corpoEmail);

		Email email = this.emailContainer.montarEmail(assunto, destinatario.trim(), corpoEmail, null);

		adicionarCopia(email, comCopia);
		this.emailContainer.enviarEmail(email);
		sucessoEnvio(assunto, comCopia, corpoEmail, destinatario, 0);
	}

	private void enviarEmailComMultiAnexo(String assunto, String destinatario, String comCopia, CorpoEmail corpoEmail,
			List<File> anexos) throws EmailException, MessagingException {
		validarTodosOsCampos(assunto, destinatario, corpoEmail);

		Email email = this.emailContainer.montarEmailMultiAnexos(assunto, destinatario.trim(), corpoEmail, anexos);

		adicionarCopia(email, comCopia);
		this.emailContainer.enviarEmail(email);
		sucessoEnvio(assunto, comCopia, corpoEmail, destinatario, anexos.size());
	}

	private void validarTodosOsCampos(String assunto, String destinatario, CorpoEmail corpoEmail)
			throws EmailException {
		validarConteudoEmail(corpoEmail);
		validarAssuntoEmail(assunto);
		validarDestinatarioEmail(destinatario);
	}

	private void adicionarCopia(Email email, String comCopia) throws EmailException {
		if (comCopia != null && !comCopia.isEmpty()) {
			email.addCc(comCopia.trim());
		}
	}

	private void validarConteudoEmail(CorpoEmail corpoEmail) throws EmailException {
		if (corpoEmail == null) {
			throw new EmailException("corpo do e-mail não foi preenchido ou foi passando de forma errada");
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

	private void falhaEnvio(String assunto, String comCopia, CorpoEmail conteudo, String destinatario,
			EmailException erro) {
		erro.printStackTrace();
		logarFalhaEnvio(assunto, destinatario, comCopia, conteudo, erro.getMessage());
		result.use(Results.http()).body("Falha no envio do e-mail: " + erro.getMessage()).setStatusCode(500);
	}

	private void sucessoEnvio(String assunto, String emailCliente, CorpoEmail conteudo, String destinatario,
			int quantidadeAnexos) {
		logarEnvio(assunto, destinatario, emailCliente, conteudo, quantidadeAnexos);
		result.use(Results.json()).withoutRoot().from("Sucesso no envio do e-mail").serialize();
	}

	private void logarEnvio(String assunto, String destinatario, String comCopia, CorpoEmail conteudo,
			int quantidadeAnexos) {
		log.info("Sucesso no envio do e-mail para o destinatário: " + destinatario);
		log.info("--- DETALHES ---");
		log.info("Assunto: " + assunto);
		log.info("Cópia: " + comCopia);
		log.info("Contéudo: " + conteudo);
		log.info("Quantidade de anexos: " + quantidadeAnexos);
	}

	private void logarFalhaEnvio(String assunto, String destinatario, String comCopia, CorpoEmail conteudo,
			String erro) {
		log.error("FALHA no envio do e-mail para o destinatário: " + destinatario);
		log.error("--- DETALHES ---");
		log.error("Assunto: " + assunto);
		log.error("Cópia: " + comCopia);
		log.error("Contéudo: " + conteudo);
		log.error("ERRO: " + erro);
	}

}