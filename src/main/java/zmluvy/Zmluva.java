/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zmluvy;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 *
 * @author janmu
 */
@JsonAutoDetect
public class Zmluva {

    private String celkova_ciastka;
    private String posledna_zmena;
    private String crz_id;
    private String cislo_zmluvy;
    private String nazov_zmluvy;
    private String dodavatel;
    private String ico_objednavatel;
    private String objednavatel;
    private String rezort;
    private String typ;
    private String subor_url;
    private String datum_zverejnenia;
    private String datum_uzavretia;
    private String datum_ucinnosti;
    private String datum_platnosti;
    private String url;
    private String ico_dodavatel;

    public Zmluva() {

    }

    public Zmluva(String datum_platnosti, String datum_ucinnosti, String datum_uzavretia, String datum_zverejnenia, String subor_url, String typ, String rezort, String objednavatel, String ico, String dodavatel, String nazov_zmluvy, String cislo_zmluvy, String crz_id, String posledna_zmena, String celkova_ciastka, String url, String ico_dodavatel) {
        this.datum_platnosti = datum_platnosti;
        this.datum_ucinnosti = datum_ucinnosti;
        this.datum_uzavretia = datum_uzavretia;
        this.datum_zverejnenia = datum_zverejnenia;
        this.subor_url = subor_url;
        this.typ = typ;
        this.rezort = rezort;
        this.objednavatel = objednavatel;
        this.ico_objednavatel = ico;
        this.dodavatel = dodavatel;
        this.nazov_zmluvy = nazov_zmluvy;
        this.cislo_zmluvy = cislo_zmluvy;
        this.crz_id = crz_id;
        this.posledna_zmena = posledna_zmena;
        this.celkova_ciastka = celkova_ciastka;
        this.url = url;
        this.ico_dodavatel = ico_dodavatel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Zmluva other = (Zmluva) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        return true;
    }

    public String getCelkova_ciastka() {
        return celkova_ciastka;
    }

    public void setCelkova_ciastka(String celkova_ciastka) {
        this.celkova_ciastka = celkova_ciastka;
    }

    public String getPosledna_zmena() {
        return posledna_zmena;
    }

    public void setPosledna_zmena(String posledna_zmena) {
        this.posledna_zmena = posledna_zmena;
    }

    public String getCrz_id() {
        return crz_id;
    }

    public void setCrz_id(String crz_id) {
        this.crz_id = crz_id;
    }

    public String getCislo_zmluvy() {
        return cislo_zmluvy;
    }

    public void setCislo_zmluvy(String cislo_zmluvy) {
        this.cislo_zmluvy = cislo_zmluvy;
    }

    public String getNazov_zmluvy() {
        return nazov_zmluvy;
    }

    public void setNazov_zmluvy(String nazov_zmluvy) {
        this.nazov_zmluvy = nazov_zmluvy;
    }

    public String getDodavatel() {
        return dodavatel;
    }

    public void setDodavatel(String dodavatel) {
        this.dodavatel = dodavatel;
    }

    public String getIco_objednavatel() {
        return ico_objednavatel;
    }

    public void setIco_objednavatel(String ico_objednavatel) {
        this.ico_objednavatel = ico_objednavatel;
    }

    public String getObjednavatel() {
        return objednavatel;
    }

    public void setObjednavatel(String objednavatel) {
        this.objednavatel = objednavatel;
    }

    public String getRezort() {
        return rezort;
    }

    public void setRezort(String rezort) {
        this.rezort = rezort;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getSubor_url() {
        return subor_url;
    }

    public void setSubor_url(String subor_url) {
        this.subor_url = subor_url;
    }

    public String getDatum_zverejnenia() {
        return datum_zverejnenia;
    }

    public void setDatum_zverejnenia(String datum_zverejnenia) {
        this.datum_zverejnenia = datum_zverejnenia;
    }

    public String getDatum_uzavretia() {
        return datum_uzavretia;
    }

    public void setDatum_uzavretia(String datum_uzavretia) {
        this.datum_uzavretia = datum_uzavretia;
    }

    public String getDatum_ucinnosti() {
        return datum_ucinnosti;
    }

    public void setDatum_ucinnosti(String datum_ucinnosti) {
        this.datum_ucinnosti = datum_ucinnosti;
    }

    public String getDatum_platnosti() {
        return datum_platnosti;
    }

    public void setDatum_platnosti(String datum_platnosti) {
        this.datum_platnosti = datum_platnosti;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIco_dodavatel() {
        return ico_dodavatel;
    }

    public void setIco_dodavatel(String ico_dodavatel) {
        this.ico_dodavatel = ico_dodavatel;
    }

  

}
