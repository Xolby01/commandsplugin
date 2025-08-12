
Le jar se trouvera ensuite dans `target/`.

## Installation
Déposez le JAR dans le dossier `plugins/` de votre serveur Paper/Spigot puis démarrez le serveur.

## Licence
MIT — utilisez/modifiez comme tu veux.



# Xolby's Commands

A simple Spigot/Paper plugin adding 3 handy commands:  
- `/craft` — opens a portable crafting table  
- `/furnace` — instantly cooks items in your inventory using registered cooking recipes (supports mod items if they register recipes in vanilla format)  
- `/ec` — opens your Ender Chest  

## Build  
Requires Java 17 and Maven.  

```bash
mvn clean package
```

The JAR will be in the `target/` folder.

## Installation

Drop the JAR into your Paper/Spigot server’s `plugins/` folder, then start your server.

## Permissions

Each command requires a permission:

* `xolby.commands.craft` — lets you use `/craft`
* `xolby.commands.furnace` — lets you use `/furnace`
* `xolby.commands.ec` — lets you use `/ec`

By default, all permissions are set to `op` only.

## License

This plugin is licensed under the MIT License. You’re free to use and modify it, but you **can’t sell it** or **claim it as your own**.
