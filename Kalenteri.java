//
// Kalenteri
// Copyright (C) Peeter Meris
//

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class Kalenteri {
    protected ArrayList<Merkinta> merkinnat;
    
    // Päivitetään kalenterin tila: tyhjennetään lista ja haetaan tietokannasta merkinnät
    public void paivita() {
        this.merkinnat.clear();
        String sql = "SELECT nimi,paikka,pvm,lisatieto FROM kalenteri ORDER BY id";
        try (Connection conn = this.yhdista(); Statement lauseke = conn.createStatement(); ResultSet tulokset = lauseke.executeQuery(sql)){
            while (tulokset.next()) {
                merkinnat.add(new Merkinta(tulokset.getString("nimi"), tulokset.getString("paikka"), tulokset.getString("pvm"), tulokset.getString("lisatieto")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Luodaan yhteys SQLite-tietokantaan
    protected Connection yhdista() {
        String url = "jdbc:sqlite:kalenteri.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;    
    }
    
    // Lisätään merkintä tietokantaan
    public void lisaaMerkinta(Merkinta m) {
        String sql = "INSERT INTO kalenteri(nimi,paikka,pvm,lisatieto) VALUES(?,?,?,?)";
        try (Connection conn = this.yhdista(); PreparedStatement lauseke = conn.prepareStatement(sql)) {
            lauseke.setString(1, m.annaNimi());
            lauseke.setString(2, m.annaPaikka());
            lauseke.setString(3, m.annaPvmJaAika());
            lauseke.setString(4, m.annaTiedot());
            lauseke.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        this.paivita();
    }
    
    // Odotetaan enterin painallusta
    public void odota() {
        System.out.println("Paina enteriä palataksesi päävalikkoon...");
        try {
            System.in.read();
        } catch (IOException e) {
        }
    }
    
    // Poistetaan käyttäjän valitsema merkintä tietokannasta ja päivitetään kalenterin tila
    protected void poistaMerkinta() {
        System.out.println();
        for(int n = 0; n < this.merkintoja(); n++) {
            Merkinta m = merkinnat.get(n);
            System.out.println((n+1) + " - " + m.annaNimi() + ", " + m.annaPaikka() + ", " + m.annaPvmJaAika() + ", " + m.annaTiedot());
        }        
        System.out.println("Valitse merkintä, jonka haluat poistaa:");
        System.out.println("Syötä 0 palataksesi päävalikkoon.");
        int valinta = -1;
        while(true) {
            valinta = this.lueValinta();
            if(valinta == 0) break;
            if(valinta >= 1 && valinta <= this.merkintoja()) {
                Merkinta m = merkinnat.get(valinta-1);
                System.out.println("Poistetaan merkintä: " + valinta + " - " + m.annaNimi() + ", " + m.annaPaikka() + ", " + m.annaPvmJaAika() + ", " + m.annaTiedot());
                
                String sql = "DELETE FROM kalenteri WHERE nimi = ? AND paikka = ? AND pvm = ? AND lisatieto = ?";
                try (Connection conn = this.yhdista(); PreparedStatement lauseke = conn.prepareStatement(sql)) {
                    lauseke.setString(1, m.annaNimi());
                    lauseke.setString(2, m.annaPaikka());
                    lauseke.setString(3, m.annaPvmJaAika());
                    lauseke.setString(4, m.annaTiedot());
                    lauseke.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                this.paivita();
                this.odota();
                break;
            }
        } 
    }
    
    // Onko kalenterissa jo merkintä haluttuna päivänä ja kellonaikana?
    public boolean onkoVarattu(String pvm, String aika) {
        boolean varattu = false;
        for(int n = 0; n < this.merkintoja(); n++) {
            Merkinta m = merkinnat.get(n);
            if(pvm.equals(m.annaPaivamaara()) && aika.equals(m.annaAika())) {
                varattu = true;
                break;
            }
        }
        return varattu;
    }
    
    // Tulostetaan päävalikko
    protected void tulostaValikko() {
        System.out.println("*** Kalenteri v.1.0 by Peeter Meris***");
        System.out.println("1 - luo uusi merkintä");
        System.out.println("2 - selaa merkintöjä");
        System.out.println("3 - poista merkintä");
        System.out.println("4 - muistutukset");
        System.out.println("5 - poistu ohjelmasta");
        System.out.println("");
        System.out.println("Valinta: ");    
    }
    
    // Luetaan käyttäjän syöttämä kokonaisluku
    protected int lueValinta() {
        int valinta = -1;
        String s = null;
        try {
            BufferedReader lukija = new BufferedReader(new InputStreamReader(System.in));
            s = lukija.readLine();
            valinta = Integer.parseInt(s);
        } catch(Exception e) { 
        } 
        return valinta;   
    }
    
    // Luetaan käyttäjän syöttämä merkkijono
    protected String lueMerkkijono() {
        String s = null;
        try {
            BufferedReader lukija = new BufferedReader(new InputStreamReader(System.in));
            s = lukija.readLine();
        } catch(Exception e) {
           e.printStackTrace(); 
        } 
        return s;
        
    }
    
    // Luetaan merkinnän nimi
    protected String lueNimi() {
        String nimi = null;
        while(nimi == "" || nimi == null || nimi.length() == 0) {
            System.out.println("Nimi:");
            nimi = lueMerkkijono();
        }
        return nimi;
    }
    
    // Luetaan merkinnän paikka
    protected String luePaikka() {
        String paikka = null;
        while(paikka == "" || paikka == null || paikka.length() == 0) {
            System.out.println("Paikka:");
            paikka = lueMerkkijono();
        }
        return paikka;
    }
    
    // Luetaan merkinnän päivämäärä ja hyväksytään vain pp.kk.vvvv muodossa oleva syöte (käytetään regexiä :) )
    protected String luePaivamaara() {
        String aika = null;
        while(aika == "" || aika == null || aika.length() == 0 || !(aika.matches("([0-9]{2}).([0-9]{2}).([0-9]{4})"))) {
            System.out.println("Päivämäärä pp.kk.vvvv:");
            aika = lueMerkkijono();
            String[] s = aika.split("\\.");
            if(s.length == 3) {
                int pp = Integer.parseInt(s[0]);
                int kk = Integer.parseInt(s[1]);
                int vvvv = Integer.parseInt(s[2]);  
                if(pp == 0 || kk == 0 || vvvv == 0 || pp > 31 || kk > 12 || vvvv < 2000 || vvvv > 2500) {
                    aika = null;
                }
            }
        }
        return aika;
    }
    
    // Luetaan käyttäjän syöttämä merkinnän kellonaika muodossa tt.mm (käytetään taas regexiä)
    protected String lueAika() {
        String aika = null;
        while(aika == "" || aika == null || aika.length() == 0 || !(aika.matches("([0-9]{2}).([0-9]{2})"))) {
            System.out.println("Kellonaika tt.mm:");
            aika = lueMerkkijono();
            String[] s = aika.split("\\.");
            if(s.length == 2) {
                int tt = Integer.parseInt(s[0]);
                int mm = Integer.parseInt(s[1]);
                if(tt > 23 || mm > 59) {
                    aika = null;
                }
            }  
        }
        return aika;
    }
    
    // Luetaan käyttäjän syöttämä merkinnän lisätieto
    protected String lueTiedot() {
        String tiedot = null;
        while(tiedot == "" || tiedot == null || tiedot.length() == 0) {
            System.out.println("Lisätiedot:");
            tiedot = lueMerkkijono();
        }
        return tiedot;
    }
    
    // Konstruktori: alustetaan lista
    public Kalenteri() {
        merkinnat = new ArrayList<Merkinta>();   
    }
    
    // Palauttaa kalenterimerkintöjen lukumäärän
    public int merkintoja() {
        return this.merkinnat.size();
    }
    
    // Palauttaa seuraavan kahden päivän merkinnät
    protected ArrayList<Merkinta> annaMuistutukset() {
        ArrayList<Merkinta> muistutukset = new ArrayList<Merkinta>();
        for(int n = 0; n < this.merkintoja(); n++) {
            Merkinta m = merkinnat.get(n);
            LocalDateTime nyt = LocalDateTime.now();
            if(m.annaVuosi() == nyt.getYear() && m.annaKuukausi() == nyt.getMonthValue() && (m.annaPaiva() >= nyt.getDayOfMonth() && m.annaPaiva() <= nyt.getDayOfMonth()+2)) {
                muistutukset.add(m);
            }
        }
        return muistutukset;
    }
    
    // Tulostaa kaikki kalenterimerkinnät
    public void tulostaMerkinnat() {
        System.out.println("Merkintöjä kalenterissa: " + this.merkintoja());
        for(int n = 0; n < merkinnat.size(); n++) {
            Merkinta m = merkinnat.get(n);
            System.out.println(m.annaNimi() + ", " + m.annaPaikka() + ", " + m.annaPvmJaAika() + ", " + m.annaTiedot());
        }
        this.odota();   
    }
    
    // Tulostaa kahden seuraavan päivän kalenterimerkinnät
    public void tulostaMuistutukset() {
        ArrayList<Merkinta> muistutukset = this.annaMuistutukset();
        System.out.println("Muistutuksia: " + muistutukset.size());
        System.out.println("Seuraavan kahden päivän merkinnät:");
        for(int n = 0; n < muistutukset.size(); n++) {
            Merkinta m = muistutukset.get(n);
            System.out.println(m.annaNimi() + ", " + m.annaPaikka() + ", " + m.annaPvmJaAika() + ", " + m.annaTiedot());
        }
        this.odota();
    }
    
    // Käynnistetään kalenteriohjelma
    public static void main(String[] args) {
        boolean ohjelmanTila = true;
        Kalenteri k = new Kalenteri();
        k.paivita();
        while(ohjelmanTila) {
            k.tulostaValikko();
            int valinta = k.lueValinta();     
            if(valinta == 1) {             
                System.out.println();
                System.out.println("Luo uusi merkintä:");
                System.out.println();
                String nimi = k.lueNimi();
                String paikka = k.luePaikka();
                String pvm = k.luePaivamaara();
                String aika = k.lueAika();
                
                if(k.onkoVarattu(pvm, aika)) {
                    System.out.println("Kalenterissa on jo merkintä kyseisenä ajankohtana!");
                    k.odota();
                } else {
                  String tiedot = k.lueTiedot();
                    Merkinta m = new Merkinta(nimi, paikka, pvm, aika, tiedot);
                    k.lisaaMerkinta(m);
                    System.out.println();
                    System.out.println("Merkintä lisätty kalenteriin:");
                    System.out.println(nimi + ", " + paikka + ", " + pvm + ", " + aika + ", " + tiedot);
                    k.paivita();
                    k.odota();  
                }   
            } else if(valinta == 2) {
                System.out.println();
                System.out.println("*Tulosta merkinnät*");
                k.tulostaMerkinnat();
                
            } else if(valinta == 3) {
                System.out.println();
                System.out.println("*Poista merkintä*");
                k.poistaMerkinta();
                k.paivita();
                
            }  else if(valinta == 4) {
                System.out.println();
                System.out.println("*Muistutukset*");
                k.tulostaMuistutukset();
                
            } else if(valinta == 5) {
                System.out.println("Poistutaan ohjelmasta...");
                ohjelmanTila = false;
            } else {
            }
        }
    }
}