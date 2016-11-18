package br.com.logique.controller.email;

import java.io.File;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
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
	public Email montarEmail(String assunto, String destinatario, String mensagem, File arquivo)
			throws EmailException, MessagingException {
		Email email = new SimpleEmail();

		if (arquivo == null) {
			email.setSubject(assunto);
			email.addTo(destinatario);
			return email;
		} else {
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.port", 587);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.user", "copergasaplicativo@gmail.com");
			properties.put("mail.password", "Copergás");

			// creates a new session with an authenticator
			Authenticator auth = new Authenticator() {
				public javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication("copergasaplicativo@gmail.com", "Copergás");
				}
			};
			Session session = Session.getInstance(properties, auth);

			// creates a new e-mail message
			Message msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress("copergasaplicativo@gmail.com"));
			InternetAddress[] toAddresses = { new InternetAddress(destinatario) };
			msg.setRecipients(Message.RecipientType.TO, toAddresses);
			msg.setSubject(assunto);
			msg.setSentDate(new Date());

			// creates message part
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(mensagem, "text/html");

			// creates multi-part
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			MimeBodyPart attachPart = new MimeBodyPart();

			try {
				attachPart.attachFile(arquivo);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			multipart.addBodyPart(attachPart);
			email.setContent(multipart);
			return email;
		}

	}

	public void enviarEmail(Email mail) throws EmailException {
		mailer.send(mail);
		// aMailer.asyncSend(mail);
	}

	public BodyPart anexarArquivo(String caminhoArquivo) throws MessagingException {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource dataSource = new FileDataSource(caminhoArquivo);
		messageBodyPart.setDataHandler(new DataHandler(dataSource));
		messageBodyPart.setHeader("Content-ID", "<" + Calendar.getInstance().getTimeInMillis() + ">");
		messageBodyPart.setFileName("Anexo");
		return messageBodyPart;
	}
}
