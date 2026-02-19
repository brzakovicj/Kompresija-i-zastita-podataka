* Kompresija i zaštita podataka

** Projekat 1
*** Opis
Projekat implementira kompresiju binarnih fajlova primenom klasičnih algoritama kompresije.

*** Funkcionalnosti
- **Bajt-entropija** - izračunava entropiju fajla na osnovu učestalosti pojavljivanja svakog bajta (0–255).
- **Algoritmi kompresije**:
  - Shannon-Fano
  - Huffman
  - LZ77
  - LZW
  Svaki algoritam omogućava: enkodiranje sadržaja fajla (`resources/test.txt`), generisanje kompresovanog fajla, dekodiranje kompresovanog fajla, generisanje dekompresovanog fajla
- **Poređenje originalnog i dekompresovanog fajla**
- **Izračunavanje stepena kompresije za svaki algoritam**

*** Struktura projekta
- `Main.java` – glavna klasa
- `algorithms/` – implementacije svih algoritama i entropije
- `structures/` – pomoćne strukture podataka
- `resources/` – ulazni i izlazni fajlovi

*** Pokretanje
1. Instalirati JDK 23 ili noviju verziju
2. Otvoriti terminal u folderu projekat_1/src
3. Kompajlirati projekat:
```bash
javac Main.java algorithms/*.java structures/*.java
```
4. Pokrenuti program:
```bash
java Main
```

*Napomena: Program koristi fajl `resources/test.txt` kao ulazni fajl.*

---

** Projekat 2

*** Opis
Projekat implementira LDPC (Low-Density Parity-Check) kod i Gallager-B algoritam za dekodiranje.

*** Funkcionalnosti
- **Generisanje LDPC kontrolne matrice H** - parametri: *n = 15, n − k = 9, wr = 5, w𝚌 = 3*
- **Generisanje tabele sindroma i korektora**
- **Određivanje minimalnog kodnog rastojanja**
- **Implementacija Gallager-B algoritma** - pragovi odlučivanja: *th0 = th1 = 0.5*
- **Određivanje minimalnog broja grešaka koje algoritam ne može da ispravi**
- **Poređenje sa teorijskom granicom ispravljanja grešaka**

*** Struktura projekta
- `Main.java` – pokretanje svih funkcionalnosti
- `LDPCCode.java` – implementacija matrice H, sindroma i Gallager-B algoritma
- `Matrix.java` i `ArrayOfBits.java` – pomoćne strukture za rad sa matricama i bit nizovima

*** Pokretanje
1. Instalirati JDK 23 ili noviju verziju
2. Otvoriti terminal u folderu projekat_2/src
3. Kompajlirati projekat:
```bash
javac *.java
```
4. Pokrenuti program:
```bash
java Main
```
