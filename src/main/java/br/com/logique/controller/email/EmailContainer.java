package br.com.logique.controller.email;

import javax.inject.Inject;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import br.com.caelum.vraptor.simplemail.Mailer;

public class EmailContainer {

	/** Mecanismo para envio de email */
	private Mailer mailer;

	public EmailContainer() {
		// TODO Auto-generated constructor stub
		this(null);
	}

	@Inject
	public EmailContainer(Mailer mailer) {
		// TODO Auto-generated constructor stub
		this.mailer = mailer;
	}

	/**
	 * Realiza montagem de um objeto email com base nos campos que compõem a
	 * mensagem completa.
	 * 
	 * @param assunto
	 * @param destinatario
	 * @param mensagem
	 * @return
	 * @throws EmailException
	 */
	public Email montarEmail(String assunto, String destinatario, String mensagem) throws EmailException{
		Email email = new SimpleEmail();
		email.setSubject(assunto);
		email.addTo(destinatario);
		email.setMsg(mensagem);
		return email;
	}	
	
	public void enviarEmail(Email mail) throws EmailException{
		mailer.send(mail);
	}
}
