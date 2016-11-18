package br.com.logique.controller.email;

import java.util.Calendar;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

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
	 * @throws MessagingException 
	 */
	public Email montarEmail(String assunto, String destinatario, String mensagem, String caminhoArquivo) throws EmailException, MessagingException{
		Email email = new SimpleEmail();
		email.setSubject(assunto);
		email.addTo(destinatario);
		email.setMsg(mensagem);
		
		if (caminhoArquivo != null) {
			MimeMultipart aMimeMultipart = new MimeMultipart();
			anexarArquivo(aMimeMultipart, caminhoArquivo);
			email.setContent(aMimeMultipart);
		}
		return email;
	}	
	
	public void enviarEmail(Email mail) throws EmailException{
		mailer.send(mail);
		//aMailer.asyncSend(mail);
	}
	
	public void anexarArquivo(MimeMultipart multipart, String caminhoArquivo) throws MessagingException{
            BodyPart messageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(caminhoArquivo);
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            messageBodyPart.setHeader("Content-ID","<"+Calendar.getInstance().getTimeInMillis()+">");
            messageBodyPart.setFileName("Anexo");
            multipart.addBodyPart(messageBodyPart);
	}
}
