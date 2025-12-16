package com.group20.perfstore.controllers;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.group20.perfstore.models.Ilan;
import com.group20.perfstore.models.IlanDto;
import com.group20.perfstore.services.IlanlarRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ilanlar")
public class IlanlarController {
	
	@Autowired
	private IlanlarRepository repo;
	
	@GetMapping({"", "/"})
	public String showIlanList(Model model) {
		List<Ilan> ilanlar = repo.findAll();
		model.addAttribute("ilanlar", ilanlar);
		return "ilanlar/index";
	}
	
	@GetMapping("/ilanekle")
	public String showCreatePage(Model model) {
		IlanDto ilanDto = new IlanDto();
		model.addAttribute("ilanDto", ilanDto);
		return "ilanlar/IlanEkle";
	}
	
	
	@PostMapping("/ilanekle")
	public String ilanEkle(
		@Valid @ModelAttribute IlanDto ilanDto,
		BindingResult result
	) {
		if(ilanDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("ilanDto", "imageFile", "Resim dosyası gereklidir"));
		}
		
		if(result.hasErrors()) {
			return"ilanlar/IlanEkle";
		}
		
		//save image file
		
		MultipartFile image= ilanDto.getImageFile();
		Date ilan_tarihi = new Date();
		String storageFileName = ilan_tarihi.getTime() + " " + image.getOriginalFilename();
		
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if(!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
						StandardCopyOption.REPLACE_EXISTING);
			}	
	    }  catch(Exception ex) {
	    	System.out.println("Exception: " + ex.getMessage()) ;
	    }
		
		String name="admin";
		Ilan ilan = new Ilan();
		ilan.setIlan_adi(ilanDto.getIlan_adi());
		ilan.setIlan_fiyati(ilanDto.getIlan_fiyati());
		ilan.setAciklama(ilanDto.getAciklama());
		ilan.setIlan_tarihi(ilan_tarihi);
		ilan.setIlan_sahibi(name);
		ilan.setImageFileName(storageFileName);
		
		repo.save(ilan);
		
		return "redirect:/ilanlar";
	}
}
