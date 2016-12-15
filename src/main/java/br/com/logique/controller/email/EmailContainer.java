package br.com.logique.controller.email;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.simplemail.AsyncMailer;

public class EmailContainer {

	private final AsyncMailer mailer;
	private final Environment ambiente;

	public EmailContainer() {
		this(null, null);
	}

	@Inject
	public EmailContainer(AsyncMailer mailer, Environment ambiente) {
		this.mailer = mailer;
		this.ambiente = ambiente;
	}

	public Email montarEmail(String assunto, String destinatario, CorpoEmail corpoEmail, File arquivo)
			throws EmailException, MessagingException {

		Email email = new HtmlEmail();
		if (arquivo == null) {
			return emailSemAnexo(assunto, destinatario, corpoEmail, email);
		} else {
			return emailComAnexo(assunto, destinatario, corpoEmail, arquivo, email);
		}

	}

	private Email emailComAnexo(String assunto, String destinatario, CorpoEmail corpoEmail, File arquivo, Email email)
			throws EmailException, MessagingException, AddressException {
		email.setSubject(assunto);
		email.addTo(destinatario);

		MimeMultipart multipart = criarMimeMultipart(assunto, destinatario, corpoEmail);

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

	private Email emailSemAnexo(String assunto, String destinatario, CorpoEmail corpoEmail, Email email)
			throws EmailException {
		email.setSubject(assunto);
		email.addTo(destinatario);
		email.setMsg(corpoEmail.toString());
		return email;
	}

	public Email montarEmailMultiAnexos(String assunto, String destinatario, CorpoEmail corpoEmail, List<File> arquivos)
			throws EmailException, MessagingException {

		if (arquivos == null || arquivos.isEmpty()) {
			Email email = new HtmlEmail();
			return emailSemAnexo(assunto, destinatario, corpoEmail, email);
		} else {
			return emailComMultiAnexos(assunto, destinatario, corpoEmail, arquivos);
		}

	}

	private Email emailComMultiAnexos(String assunto, String destinatario, CorpoEmail corpoEmail, List<File> arquivos)
			throws EmailException, MessagingException, AddressException {
		Email email = new SimpleEmail();
		email.setSubject(assunto);
		email.addTo(destinatario);

		MimeMultipart multipart = criarMimeMultipart(assunto, destinatario, corpoEmail);

		for (File arquivo : arquivos) {
			MimeBodyPart attachPart = new MimeBodyPart();
			try {
				attachPart.attachFile(arquivo);
				multipart.addBodyPart(attachPart);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		email.setContent(multipart);
		return email;
	}

	private MimeMultipart criarMimeMultipart(String assunto, String destinatario, CorpoEmail corpoEmail)
			throws MessagingException, AddressException {
		final String usuarioEmail = ambiente.get("vraptor.simplemail.main.username", "copergasaplicativo@gmail.com");
		final String senha = ambiente.get("vraptor.simplemail.main.password", "c0perg@s");

		Properties properties = carregarPropriedadesEmail(usuarioEmail, senha);

		Session session = criarSessionEmail(usuarioEmail, senha, properties);

		Message msg = iniciarMessageEmail(assunto, destinatario, usuarioEmail, session);

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(corpoEmail.toString(), "text/html; charset=utf-8");

		MimeMultipart multipart = new MimeMultipart("alternative");
		multipart.addBodyPart(messageBodyPart);

		msg.setContent(multipart);
		msg.saveChanges();

		return multipart;
	}

	private Message iniciarMessageEmail(String assunto, String destinatario, final String usuarioEmail, Session session)
			throws MessagingException, AddressException {
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(usuarioEmail));
		InternetAddress[] toAddresses = { new InternetAddress(destinatario) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(assunto);
		msg.setSentDate(new Date());
		return msg;
	}

	private Session criarSessionEmail(final String usuarioEmail, final String senha, Properties properties) {
		Authenticator auth = new Authenticator() {
			public javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(usuarioEmail, senha);
			}
		};
		Session session = Session.getInstance(properties, auth);
		return session;
	}

	private Properties carregarPropriedadesEmail(final String usuarioEmail, final String senha) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", ambiente.get("vraptor.simplemail.main.server", "smtp.gmail.com"));
		properties.put("mail.smtp.port", ambiente.get("vraptor.simplemail.main.port", "587"));
		properties.put("mail.smtp.auth", ambiente.get("", "true"));
		properties.put("mail.smtp.starttls.enable", ambiente.get("vraptor.simplemail.main.tls", "true"));
		properties.put("mail.user", usuarioEmail);
		properties.put("mail.password", senha);
		return properties;
	}

	public void enviarEmail(Email mail) throws EmailException {
		mailer.asyncSend(mail);
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
