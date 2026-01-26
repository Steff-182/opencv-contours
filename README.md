# OpenCV Contours - PDF Image Processing

Un projet Java 25 pour extraire des images de PDF et détecter les contours de documents (cas d'usage : cartes d'identité numérisées, documents scannés).

## Fonctionnalités

- **Extraction d'images depuis PDF** : Utilise PDFBox pour extraire les pages PDF en images de haute qualité (300 DPI)
- **Détection de contours** : Utilise OpenCV pour détecter les contours principaux des documents
- **Cropping automatique** : Élimine les zones blanches inutiles autour du document
- **Support Java 25** : Construit avec Maven et Java 25

## Structure du projet

```
opencv-contours/
├── src/
│   ├── main/
│   │   └── java/com/opencv/
│   │       ├── demo/
│   │       │   └── Main.java              # Classe de démonstration
│   │       └── processing/
│   │           ├── PDFImageExtractor.java # Extraction d'images depuis PDF
│   │           └── ContourDetector.java   # Détection de contours avec OpenCV
│   └── test/
│       └── resources/                     # Placez vos fichiers PDF ici
├── pom.xml                                # Configuration Maven
└── README.md
```

## Dépendances

- **Java 25** : Langage de programmation
- **Maven** : Gestion des dépendances
- **OpenCV 4.8.1-0** : Traitement d'images et détection de contours
- **PDFBox 2.0.29** : Extraction d'images depuis PDF
- **SLF4J + Simple** : Logging

## Installation et utilisation

### 1. Placer un PDF de test

Placez un fichier PDF dans `src/test/resources/`. Par exemple :
```
src/test/resources/carte_identite.pdf
src/test/resources/document.pdf
```

### 2. Compiler le projet

```bash
mvn clean compile
```

### 3. Exécuter la démonstration

```bash
mvn exec:java -Dexec.mainClass="com.opencv.demo.Main"
```

Ou simplement :

```bash
java -cp target/classes:target/opencv-contours-jar-with-dependencies.jar com.opencv.demo.Main
```

### 4. Résultats

Les fichiers de sortie sont générés dans `target/output/` :
- `01_extracted.png` : Image extraite du PDF
- `02_contours_detected.png` : Image avec contours détectés
- `03_cropped.png` : Image finale après cropping

## Utilisation par le code

### Extraction d'image depuis PDF

```java
BufferedImage image = PDFImageExtractor.extractFirstPage("chemin/vers/fichier.pdf");
```

### Détection de contours

```java
Mat contourImage = ContourDetector.detectDocumentContour(bufferedImage);
```

### Cropping automatique

```java
BufferedImage croppedImage = ContourDetector.cropToDocument(bufferedImage);
```

## Cas d'usage

Ce projet est particulièrement adapté pour :
- Scanner automatique de cartes d'identité
- Traitement de documents numérisés sur fond blanc
- Extraction de zones pertinentes depuis des scans
- Normalisation de contours de documents

## Performance

- Extraction à 300 DPI pour meilleure qualité
- Traitement rapide même pour des images haute résolution
- Optimisé pour documents sur fond blanc (scans standards)

## Notes techniques

- Les images sont converties en niveaux de gris pour la détection
- Application de filtre Gaussien pour réduire le bruit
- Seuillage binaire inverse pour isoler le document
- Opérations morphologiques (closing) pour nettoyer les contours
- Contours triés par aire de surface (le plus grand = document principal)

## Licence

Libre d'utilisation

## Auteur

Généré avec OpenCV et PDFBox
