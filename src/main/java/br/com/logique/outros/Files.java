package br.com.logique.outros;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.logique.controller.EmailController;

@Controller
public class Files {
	
	private static final String PASTA_UPLOAD = "c:\\api-email\\anexos";
	
	private File fileSave;
	File diretory = null;
	
	Logger log = Logger.getLogger(Files.class);

	public Files() {
	}

	public File searchFile() {
		if (diretory == null)
			diretory = new File(PASTA_UPLOAD);
		return diretory;
	}

	public File save(UploadedFile file) {		
		fileSave = new File(searchFile(), file.getFileName());
		try {
			if (!searchFile().exists()) {
				
				if(searchFile().mkdirs()){
					log.info("DIRETORIO DE ANEXOS CRIADO");
				} else {
					log.error("FALHA AO CRIAR DIRETORIO DE ANEXOS");
				}
			}
				
			IOUtils.copyLarge(file.getFile(), new FileOutputStream(fileSave));
			return fileSave;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getPath(){
		String path = fileSave.getPath();
		return path;
	}
	
	public String getNameFile(){
		String name = fileSave.getName();
		return name;
	}
	
}
