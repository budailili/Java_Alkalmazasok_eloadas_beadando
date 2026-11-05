# Árfolyam és Forex kliens – Java alkalmazások beadandó

Böngésző alapú alkalmazás Java 25 + Spring Boot keretrendszerrel.

**Készítők**
- Budai Lili (GVLRPX)
- Bácskai József Kristóf (GHX0DH)

## Menük

| Menü | Leírás |
|---|---|
| Főoldal | Az alkalmazás bemutatása |
| SOAP | MNB árfolyamok devizára és időszakra, táblázat + Chart.js grafikon |
| Forex-account | Az Oanda demo számla adatai |
| Forex-AktÁr | Instrumentum aktuális ára |
| Forex-HistÁr | Az utolsó 10 historikus ár választható granularity-vel |
| Forex-Nyit | Pozíció nyitása piaci áron (pozitív mennyiség: Long, negatív: Short) |
| Forex-Poz | Nyitott pozíciók táblázatban |
| Forex-Zár | Pozíció zárása tradeId alapján |

## Futtatás

```
java -jar target\beadando.jar
```

Utána böngészőben: http://localhost:8080

## Build (Maven nélkül is)

A projektben benne van a Maven Wrapper, ezért külön Maven telepítés nem kell:

```
./mvnw clean package        (Mac / Linux)
.\mvnw.cmd clean package      (Windows)
```

Az eredmény: `target/beadando.jar`.

## Oanda beállítás

A Forex menükhöz egy `oanda.properties` fájl kell az `oanda.properties.example` minta alapján.
Az alkalmazás először a `beadando.jar` melletti mappában keresi, utána a `src/main/resources` mappában.
A fájl a `.gitignore` miatt nem kerül fel a GitHubra.

## Felhasznált eszközök

- Java 25, Spring Boot 3.5, Thymeleaf
- JAX-WS (MNB `arfolyamok.asmx` SOAP szolgáltatás, a kliensosztályok `wsimport`-tal generálva)
- Oanda v20 Java kliens
- Chart.js
- Téma: Bootswatch Flatly (https://bootswatch.com/flatly/, MIT licenc)
