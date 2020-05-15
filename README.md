# Kalenteri
Javalla ja SQLitellä toteutettu komentorivipohjainen kalenteri. Vaatii toimiakseen JDBC SQlite -ajurin (esim. sqlite-jdbc-3.30.1.jar).

Tietokanta on luotu komennolla:
CREATE TABLE kalenteri (id INTEGER PRIMARY KEY, nimi TEXT NOT NULL, paikka TEXT NOT NULL, pvm TEXT NOT NULL, lisatieto TEXT NOT NULL);

Ohjelma käynnistetään komennolla:
java -classpath ".:sqlite-jdbc-3.30.1.jar" Kalenteri
