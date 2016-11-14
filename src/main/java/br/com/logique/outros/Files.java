package br.com.logique.outros;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.observer.upload.UploadedFile;

@Controller
public class Files {
	
	private static final String PASTA_UPLOAD = "c:\\upload_documentos\\";
	
	private File fileSave;

	public Files() {
	}

	public File searchFile() {
		File diretory;
		diretory = new File(PASTA_UPLOAD);
		return diretory;
	}

	public void save(UploadedFile file) {		
		fileSave = new File(searchFile(), file.getFileName());
		try {
			IOUtils.copyLarge(file.getFile(), new FileOutputStream(fileSave));
		} catch (IOException e) {
			e.printStackTrace();
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
