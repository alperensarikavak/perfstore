package com.group20.perfstore.models;

import java.util.Date;

import jakarta.persistence.*;
@Entity
@Table(name="ilanlar")
public class Ilan {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	private String ilan_adi;
	private String ilan_sahibi;
	private double ilan_fiyati;
	
	@Column(columnDefinition = "TEXT")
	private String aciklama;
	private Date ilan_tarihi;
	private String imageFileName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIlan_adi() {
		return ilan_adi;
	}
	public void setIlan_adi(String ilan_adi) {
		this.ilan_adi = ilan_adi;
	}
	public String getIlan_sahibi() {
		return ilan_sahibi;
	}
	public void setIlan_sahibi(String ilan_sahibi) {
		this.ilan_sahibi = ilan_sahibi;
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
	public Date getIlan_tarihi() {
		return ilan_tarihi;
	}
	public void setIlan_tarihi(Date ilan_tarihi) {
		this.ilan_tarihi = ilan_tarihi;
	}
	public String getImageFileName() {
		return imageFileName;
	}
	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}
	
	
}
