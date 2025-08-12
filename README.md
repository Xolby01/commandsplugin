# Xolby's Commands

Plugin Spigot/Paper simple ajoutant 3 commandes:
- `/craft` : ouvre une table de craft portable
- `/furnace` : cuit instantanément les items dans l'inventaire du joueur en utilisant
  les recettes de cuisson enregistrées (supporte les items ajoutés par des mods si ceux-ci
  enregistrent leurs recettes au format vanilla)
- `/ec` : ouvre l'Ender Chest du joueur

## Build
Requis: Java 17, Maven.

```bash
mvn clean package
```

Le jar se trouvera ensuite dans `target/`.

## Installation
Déposez le JAR dans le dossier `plugins/` de votre serveur Paper/Spigot puis démarrez le serveur.

## Licence
MIT — utilisez/modifiez comme tu veux.
