# Murder Plugin

MurderPlugin is a Minecraft 1.15.2 plugin to play a murder game in Minecraft (like the one in Garry's Mod).

## Installation

Download the file "out/MurderPlugin.jar"

Put the file in the "plugins" directory on you Minecraft 1.15.2 server.

Type in the game "/reload" or "reload" in the server console to load the plugin or restart your server.

## Usage

To use it, you need to set up spawnpoint on your map, with the command "/m spawnpoint|sp <id> <x> <y> <z>".
You can also use the small version : "/m addSpawnpointHere|add" to add a spawnpoint on the player position.
The plugin need only one spawnpoint to start. I advise you to define as many spawnpoints as there are players (or more).

You can save spawnpoints with : "/m saveSpawnpoints|save" and load them after a plugin or a server restart with : "/m loadSpawnpoints|load"

Then, you can type "/m start" to start the game, and all online players will be teleported randomly to the spawnpoint.
The "/m stop" command will stop the game and teleport all player to the map spawn.

## Download it, Try it, and give me feed backs !

## License
[MIT](https://choosealicense.com/licenses/mit/)

## Versions

1.0 - Game working
1.1 - Save settings
1.2 - Glowing
1.3 - Tp at spawn if player falling out of world + difficulty peaceful auto
1.4 - Add statistics
1.5 - Points