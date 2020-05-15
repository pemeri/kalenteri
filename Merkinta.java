//
// Kalenteri
// Copyright (C) Peeter Meris
//

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Merkinta {
    
    String nimi;
    String paikka;
    LocalDateTime pvm;
    String tiedot;
    
    // Muuttaa String-tyyppisen päivämäärän ja kellonajan LocalDateTime-olioksi
    public LocalDateTime merkkijonoPaivamaaraksi(String p, String a) {
        LocalDateTime ltd = LocalDateTime.now();
        String[] s = p.split("\\.");
        String[] t = a.split("\\.");
        if(s.length == 3 && t.length == 2) {
            int pp = Integer.parseInt(s[0]);
            int kk = Integer.parseInt(s[1]);
            int vvvv = Integer.parseInt(s[2]);
            int tt = Integer.parseInt(t[0]);
            int mm = Integer.parseInt(t[1]);
            ltd = LocalDateTime.of(vvvv, kk, pp, tt, mm);
        }
        return ltd;
    }
    
    // Konstruktori
    public Merkinta(String nimi, String paikka, String pvm, String aika, String tiedot) {
        this.nimi = nimi;
        this.paikka = paikka;
        this.pvm = merkkijonoPaivamaaraksi(pvm, aika); 
        this.tiedot = tiedot;
    }
    
    // Konstruktori 2
    public Merkinta(String nimi, String paikka, String pvmJaAika, String tiedot) {
        this.nimi = nimi;
        this.paikka = paikka;
        this.pvm = LocalDateTime.parse(pvmJaAika, DateTimeFormatter.ofPattern("dd.MM.uuuu HH.mm")); //LocalDateTime.parse(a, DateTimeFormatter.ofPattern("dd.MM.uuuu")); 
        this.tiedot = tiedot;
    }
    
    // Palauttaa merkinnän nimen
    public String annaNimi() {
        return this.nimi;
    }
    
    // Palauttaa merkinnän paikan
    public String annaPaikka() {
        return this.paikka;
    }
    
    // Palauttaa merkinnän lisätiedon
    public String annaTiedot() {
        return this.tiedot;
    }
    
    // Palauttaa merkinnän päivämäärän ja kellonajan
    public String annaPvmJaAika() {
        return this.pvm.format(DateTimeFormatter.ofPattern("dd.MM.uuuu HH.mm"));
    }
    
    // Palauttaa merkinnän päivämäärän
    public String annaPaivamaara() {
        return this.pvm.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
    }
    
    // Palauttaa merkinnän kellonajan
    public String annaAika() {
        return this.pvm.format(DateTimeFormatter.ofPattern("HH.mm"));
    }
    
    // Palauttaa merkinnän päivämäärästä vain vuosiluvun
    public int annaVuosi() {
        return this.pvm.getYear();
    }
    
    // Palauttaa merkinnän päivämäärästä vain kuukauden
    public int annaKuukausi() {
        return this.pvm.getMonthValue();
    }
    
    // Palauttaa merkinnän päivämäärästä vain päivän
    public int annaPaiva() {
        return this.pvm.getDayOfMonth();
    }
    
}