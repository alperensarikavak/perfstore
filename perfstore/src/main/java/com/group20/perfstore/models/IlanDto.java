package com.group20.perfstore.models;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class IlanDto {
	@NotEmpty(message = "İlan adı zorunludur")
	private String ilan_adi;
	
	@Min(0)
	private double ilan_fiyati;
	
	@Size(min = 10, message = "İlan aciklaması en az 10 karakterden oluşmalıdır")
	@Size(max = 150, message = "İlan aciklaması en fazla 150 karakterden oluşmalıdır")
	private String aciklama;
	
	public String getIlan_adi() {
		return ilan_adi;
	}

	public void setIlan_adi(String ilan_adi) {
		this.ilan_adi = ilan_adi;
	}

	public double getIlan_fiyati() {
		return ilan_fiyati;
	}

	public void setIlan_fiyati(double ilan_fiyati) {
		this.ilan_fiyati = ilan_fiyati;
	}

	public String getAciklama() {
		return aciklama;
	}

	public void setAciklama(String aciklama) {
		this.aciklama = aciklama;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	private MultipartFile imageFile;
}
