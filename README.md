# Home-Banking Application

<p align="center">
  <img src="https://github.com/CosimoGiani/SWAM_home_banking/blob/main/images/logo.png" style="width:800px;">
</p>

Progettazione e implementazione di un'applicazione realizzata in architettura RESTful con un contesto operativo di home-banking. \
Progetto per l'esame di Software Architecture and Methodologies (SWAM) tenuto dal prof. Enrico Vicario, in collaborazione con il Dott. Boris Brizzi, l’Ing. Jacopo Parri e l’Ing. Samuele Sampietro, e previsto dal corso di laurea magistrale in Ingegneria Informatica dell'Università degli Studi di Firenze, A.A. 2021/2022.

## Funzionalità
L'intento dell'elaborato è stato quello di progettare e implementare un'applicazione in architettura RESTful, composta principalmente da un modulo di backend, che espone i servizi REST e si interfaccia ad un DBMS. Il contesto operativo è stato quello dell'Home-banking come pretesto per lo sviluppo.

La banca **PSB** (**P**arri - **S**ampietro - **B**rizzi) ha commissionato una Home-Banking Application che permetta ad un *Cliente* di aprire e gestire un *Conto Corrente* comodamente da casa. \
Nello specifico, il Cliente può scegliere tra diverse *Tipologie* di Conto Corrente offerte da PSB (conto Under30, conto Ordinario o conto In-vestitore) e aprire uno o più conti delle tipologie indicate. Ciascun conto si differenzia in termini di Costi, Tassi d’interesse e Massimali di prelievo. Ad ogni conto può essere associata una o più *Carte* di diversa tipologia (Debito, Credito o Ricaricabile), dove ciascuna presenta un numero di carta, una data di scadenza e un massimale mensile. \
All’interno dell’applicazione l’utente può visualizzare le *Transazioni* effettuate sul proprio conto e i loro dettagli. 

Al momento della registrazione viene richiesto al Cliente di aggiungere i suoi dati anagrafici: questo può essere fatto **caricando un PDF** apposito da lui compilato che li contenga e che **vengono estratti automaticamente** dall’applicazione. \
Per motivi di sicurezza PSB richiede che vi sia un’**autenticazione a due fattori** per accedere alla propria area personale. \
Può essere **attiva un’unica sessione alla volta**: se un utente si logga da un altro device con un’altra sessione già aperta, quest’ultima si deve chiudere ed essere trasferita al nuovo dispositivo. 

A ciascun Cliente, al momento della registrazione, viene associato un *Consulente* bancario che si occupa di consigliare nuove offerte e fornire assistenza in caso di investimenti qualora il Cliente ne abbia necessità. Un Consulente bancario può vedere i dettagli dei clienti cui è referente, così da poter fornire supporto.

In una prospettiva di progettazione avanzata, l'applicazione di backend è stata validata opportunamente tramite un **modulo/framework di testing** finalizzato a "*stressare*" i vari endpoint REST. 

Per maggiori informazioni e una descrizione completa dell'applicazione si rimanda alla lettura del [report](https://github.com/CosimoGiani/SWAM_home_banking/blob/main/Relazione%20SWAM%20Giani-Pucci.pdf).

## Tecnologie utilizzate
L'applicazione di backend è stata implementata tramite:
* le specifiche di [`Jakarta EE`](https://jakarta.ee/) in Java
* interfacciandosi al **DBMS** [`MariaDB`](https://mariadb.com/) per la persistenza dei dati
* da mandare in esecuzione su un Application Server che nell'elaborato in questione è consistito in [`WildFly`](https://www.wildfly.org/)

Il modulo di testing è stato invece realizzato facendo uso dei seguenti framework:
* [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
* [Mockito](https://site.mockito.org/)
* [RestAssured](https://rest-assured.io/)
