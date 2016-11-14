package br.com.logique.controller;

import java.io.Serializable;

import javax.inject.Inject;

import org.apache.commons.mail.EmailException;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.view.Results;
import br.com.logique.controller.email.EmailContainer;

@Controller
@Path("/email")
public class EmailController implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Result result;
	private EmailContainer emailContainer;
	private Environment ambiente;

	public EmailController() {
		this(null, null, null);
	}

	@Inject
	public EmailController(Result result, EmailContainer emailContainer, Environment ambiente) {
		this.result = result;
		this.emailContainer = emailContainer;
		this.ambiente = ambiente;
	}
	
	@Post("/emergencia-falta-gas")
	public void enviarEmail(String conteudo){
		try {
			this.emailContainer.enviarEmail(this.emailContainer.montarEmail("[EMERGÊNCIA] Falta de Gás", ambiente.get("vraptor.simplemail.main.from"), conteudo));
			result.use(Results.json()).from("ok", "Sucesso no envio do e-mail");
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	

}