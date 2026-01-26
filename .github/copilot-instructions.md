## Projet Maven Java 25 - OpenCV Contours & PDF Processing

Ce projet extrait des images de fichiers PDF et détecte les contours de documents numérisés (cartes d'identité, documents scannés).

### Structure du projet

```
├── src/
│   ├── main/java/com/opencv/
│   │   ├── demo/Main.java - Classe de démonstration
│   │   └── processing/
│   │       ├── PDFImageExtractor.java - Extraction d'images PDF
│   │       └── ContourDetector.java - Détection de contours OpenCV
│   └── test/resources/ - Placez vos PDF ici
├── pom.xml - Configuration Maven avec OpenCV et PDFBox
├── README.md - Documentation complète
└── QUICKSTART.md - Guide de démarrage rapide
```

### Dépendances principales

- **Java 25** - JDK 25
- **OpenCV 4.8.1** - Traitement d'images (openpnp binding)
- **PDFBox 2.0.29** - Extraction d'images PDF
- **SLF4J 2.0.11** - Logging

### Commandes utiles

**Compilation :**
```bash
mvn clean compile
```

**Exécution :**
```bash
mvn exec:java -Dexec.mainClass="com.opencv.demo.Main"
```

**Package JAR :**
```bash
mvn package
```

**Tests :**
```bash
mvn test
```

### Utilisation rapide

1. Placez un PDF dans `src/test/resources/`
2. Exécutez la démo
3. Consultez les résultats dans `target/output/`

Voir [QUICKSTART.md](QUICKSTART.md) pour plus de détails.

