# Companion for map mods

<p>
  <a href="https://modrinth.com/plugin/modmapcompanion">
    <img src="https://modrinth-utils.vercel.app/api/badge/downloads?id=UO7aDcrF&logo=true" />
  </a>
  <a href="https://www.curseforge.com/minecraft/bukkit-plugins/mapmodcompanion">
    <img src="https://cf.way2muchnoise.eu/full_674380_downloads.svg">
  </a>
  <a href="https://www.spigotmc.org/resources/mapmodcompanion.105128/">
    <img src="https://pluginbadges.glitch.me/api/v1/dl/Spigot%20downloads-ed8107.svg?spigot=105128">
  </a>
</p>

Unofficial BungeeCord and Spigot (Paper) companion plugin for
[Xaero's Minimap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap)
(and their [World Map](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map)),
[JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap) and
VoxelMap (both [old](https://www.curseforge.com/minecraft/mc-mods/voxelmap) and [updated](https://modrinth.com/mod/voxelmap-updated)).

This plugin fixes Multi-world detection in multiplayer by simulating mod presence on
the server side.


It's recommended to install this plugin on a fresh server, otherwise **existing map data**
(waypoints, map cache, etc.) **may no longer be visible to some players**. Fortunately,
[there are ways to restore it](https://github.com/turikhay/MapModCompanion/wiki/Restore-map-data).
It's worth mentioning that the plugin doesn't affect in-game progress.

This plugin was inspired by @kosma's [worldnamepacket](https://github.com/kosma/worldnamepacket),
which supported Velocity, Fabric and Spigot at the time of writing.

If you have any questions, please [join my Discord](https://discord.gg/H9ACHEqBrg).

## Support table
| Mod                                                                                | Oldest version             | Latest version                                               | Status      |
|------------------------------------------------------------------------------------|----------------------------|--------------------------------------------------------------|-------------|
| [Xaero's Minimap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap)     | v20.20.0 / Minecraft 1.8.9 | v22.x.x / Minecraft 1.19.3                                   | ✅ Supported |
| [Xaero's World Map](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map) | v1.10.0 / Minecraft 1.8.9  | v1.28.x / Minecraft 1.19.3                                   | ✅ Supported |
| [JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap)              | v5.7.1 / Minecraft 1.16.5  | v5.9.2 / Minecraft 1.19.3                               | ✅ Supported |
| VoxelMap                                                                           | [v1.7.10](https://www.curseforge.com/minecraft/mc-mods/voxelmap) / Minecraft 1.8    | [v1.12.0](https://modrinth.com/mod/voxelmap-updated) / Minecraft 1.19.3 | ✅ Supported |

## Installation
1. Download the latest release from [Releases](https://github.com/turikhay/MapModCompanion/releases) page
2. Put each file into the corresponding plugins folder
3. That's it. No configuration is required. You can restart your servers now.

⚠️ **NOTE** Spigot plugin can be used without BungeeCord counterpart, but it's highly recommended to install
plugins on both sides. On the contrary, BungeeCord plugin is useless if you don't install Spigot plugin on
downstream servers.

## Alternatives
- If you're running Forge or Fabric server, just install the map mod on your server: this will unlock all its
  features.
- [worldnamepacket](https://github.com/kosma/worldnamepacket) (Velocity, Fabric, Spigot)
- [journeymap-bukkit](https://github.com/TeamJM/journeymap-bukkit) (Spigot)
- [JourneyMap Server](https://www.curseforge.com/minecraft/mc-mods/journeymap-server) (Spigot)
- [Minimap server](https://github.com/Ewpratten/MinimapServer) (Spigot)
