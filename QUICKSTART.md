# Guide de Démarrage Rapide

## 1️⃣ Installation des prérequis

Assurez-vous d'avoir Java 25 et Maven installés :

```bash
java -version
mvn -version
```

## 2️⃣ Préparation des fichiers PDF

Placez vos fichiers PDF de test dans le répertoire `src/test/resources/` :

```
src/test/resources/
├── carte_identite.pdf
├── document.pdf
└── scan.pdf
```

Les fichiers PDF peuvent être :
- Cartes d'identité numérisées
- Documents scannés sur fond blanc
- Tout document avec une zone principale sur fond blanc

## 3️⃣ Compilation du projet

```bash
mvn clean compile
```

Ou utilisez la tâche VS Code : `Build OpenCV Contours`

## 4️⃣ Exécution de la démonstration

### Option 1 : Avec Maven

```bash
mvn exec:java -Dexec.mainClass="com.opencv.demo.Main"
```

### Option 2 : Avec la tâche VS Code

Appuyez sur `Ctrl+Shift+B` ou utilisez la palette de commandes (`Ctrl+Shift+P`) et sélectionnez "Run Demo"

### Option 3 : Package et exécution

```bash
mvn package
java -jar target/opencv-contours-jar-with-dependencies.jar
```

## 5️⃣ Résultats

Les fichiers de sortie sont générés dans `target/output/` :

```
target/output/
├── 01_extracted.png      # Image extraite du PDF
├── 02_contours_detected.png  # Image avec contours détectés (vert)
└── 03_cropped.png        # Image finale after cropping
```

## 📊 Cas d'usage typiques

### Carte d'identité
```
Source : Scan d'une carte d'identité sur scanner blanc
Résultat : Zone de la carte détourée, arrière-plan blanc supprimé
```

### Document
```
Source : PDF d'un document scanné
Résultat : Contours du document détectés, zones inutiles supprimées
```

## 🔧 Utilisation dans votre code

### Extraction simple

```java
BufferedImage image = PDFImageExtractor.extractFirstPage("chemin/vers/file.pdf");
```

### Détection de contours

```java
Mat contours = ContourDetector.detectDocumentContour(bufferedImage);
```

### Cropping automatique

```java
BufferedImage cropped = ContourDetector.cropToDocument(bufferedImage);
```

### Sauvegarde des résultats

```java
ContourDetector.saveMat(mat, "output.png");
ContourDetector.saveBufferedImage(bufferedImage, "output.png");
```

## 📝 Logs et debugging

Le projet utilise SLF4J avec SimpleSLF4J. Les logs sont affichés dans la console :

```
[INFO] Processing PDF: document.pdf
[INFO] Successfully extracted image: 2550x3300 pixels
[INFO] Detecting document contour...
[INFO] Largest contour area: 8250000.0
[INFO] Image saved to: target/output/02_contours_detected.png
```

## ⚠️ Troubleshooting

### "No PDF files found"
→ Vérifiez que votre PDF est bien dans `src/test/resources/`

### "Failed to render image"
→ Le PDF peut être corrompu ou au format protégé

### "No contours found"
→ L'image peut ne pas avoir de contraste suffisant. Les algorithmes s'attendent à un document sur fond blanc.

## 🎯 Performance

- **Extraction** : ~200ms par page (300 DPI)
- **Détection de contours** : ~50-100ms selon la résolution
- **Cropping** : ~30ms

## 📚 Documentation complète

Consultez [README.md](README.md) pour la documentation complète du projet.
