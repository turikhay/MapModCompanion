# Companion for map mods

<p>
  <a href="https://github.com/turikhay/MapModCompanion/blob/main/LICENSE.txt">
    <img src="https://img.shields.io/github/license/turikhay/MapModCompanion">
  </a>
  <a href="https://github.com/turikhay/MapModCompanion/actions/workflows/e2e_notable.yml">
    <img src="https://github.com/turikhay/MapModCompanion/actions/workflows/e2e_notable.yml/badge.svg" />
  </a>
  <a href="https://www.spigotmc.org/resources/mapmodcompanion.105128/">
    <img src="https://img.shields.io/spiget/downloads/105128?label=Spigot%20%28downloads%29">
  </a>
  <a href="https://modrinth.com/plugin/modmapcompanion">
    <img src="https://img.shields.io/modrinth/dt/UO7aDcrF?label=Modrinth%20%28downloads%29" />
  </a>
  <a href="https://www.curseforge.com/minecraft/bukkit-plugins/mapmodcompanion">
    <img src="https://cf.way2muchnoise.eu/full_674380_downloads.svg">
  </a>
  <a href="https://www.buymeacoffee.com/turikhay">
    <img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" height="20px">
  </a>
</p>

<img
  align="right"
  width="200"
  height="200"
  src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/allaylogo3_1000_10.png"
  alt="Allay from Minecraft holding a compass and waving with their other hand at the viewer"
/>

**With this plugin your minimap will never be confused which world you're in.**

<details><summary>How it should look like</summary>

| Mod | Screenshot |
| ----|------------|
| Xaero's WorldMap | <img src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/2023-03-28_00.32.04_1.png" width="200" alt="Screenshot of Xaero's WorldMap menu" /> |
| VoxelMap | <img src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/2023-03-28_00.34.50_1.png" width="200" alt="Screenshot of the game with a minimap on the top-right corner" /> <img src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/2023-03-28_00.35.04_1.png" width="200" alt="Screenshot of a map" /> |
| Xaero's Minimap | See Xaero's WorldMap |
| JourneyMap | It just works 😄 |

</details> 

Companion plugin for
[Xaero's Minimap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap)
(and their [World Map](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map)),
[JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap) and
VoxelMap (both [old](https://www.curseforge.com/minecraft/mc-mods/voxelmap) and [updated](https://modrinth.com/mod/voxelmap-updated)).
Provides a way for these mods to identify worlds on BungeeCord/Velocity servers.

It's recommended to install this plugin on a fresh server, otherwise **existing map data**
(waypoints, map cache, etc.) **may no longer be visible to some players**. Fortunately,
[there are ways to restore it](https://github.com/turikhay/MapModCompanion/wiki/Restore-map-data).
It's worth mentioning that the plugin doesn't affect in-game progress.

This plugin was inspired by @kosma's [worldnamepacket](https://github.com/kosma/worldnamepacket),
which supported Velocity, Fabric and Spigot at the time of writing.

If you have any questions, please [join my Discord](https://discord.gg/H9ACHEqBrg).

[![](https://bstats.org/signatures/bukkit/MapModCompanion.svg)](https://bstats.org/plugin/bukkit/MapModCompanion/16539 "MapModCompanion on bStats")

## Support table
| Mod                                                                                | Oldest version             | Latest version                                               | Status      |
|------------------------------------------------------------------------------------|----------------------------|--------------------------------------------------------------|-------------|
| [Xaero's Minimap](https://www.curseforge.com/minecraft/mc-mods/xaeros-minimap)     | v20.20.0 / Minecraft 1.8.9 | v23.x.x / Minecraft 1.19.4                                   | ✅ Supported |
| [Xaero's World Map](https://www.curseforge.com/minecraft/mc-mods/xaeros-world-map) | v1.10.0 / Minecraft 1.8.9  | v1.30.x / Minecraft 1.19.4                                   | ✅ Supported |
| [JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap)              | v5.7.1 / Minecraft 1.16.5  | v5.9.5 / Minecraft 1.19.4                               | ✅ Supported |
| VoxelMap                                                                           | [v1.7.10](https://www.curseforge.com/minecraft/mc-mods/voxelmap) / Minecraft 1.8    | [v1.12.x](https://modrinth.com/mod/voxelmap-updated) / Minecraft 1.19.4 | ✅ Supported<sup class="reference">[[1]](https://github.com/turikhay/MapModCompanion/issues/8)</sup> |


## Installation

ℹ️ You should install this plugin on both sides: on BungeeCord/Velocity and on Spigot/Paper.

1. Download the latest release from [Releases](https://github.com/turikhay/MapModCompanion/releases) page
2. Put each file into the corresponding plugins folder
3. That's it. No configuration is required. You can restart your servers now.

⚠️ **NOTE** Spigot plugin may not work without BungeeCord/Velocity counterpart. I highly recommend installing
plugins on both sides. BungeeCord/Velocity plugin is useless if you don't install Spigot plugin on
downstream servers.

## Configuration
The configuration file is stored at `plugins/MapModCompanion/config.yml` for both Spigot and BungeeCord.
Velocity uses `plugins/mapmodcompanion/config.toml`.

The configuration file reloads automatically if it's modified.

## Alternatives
- If you're running Forge or Fabric server, just install the map mod on your server: this will unlock all its
  features.
- [worldnamepacket](https://github.com/kosma/worldnamepacket) (Velocity, Fabric, Spigot)
- [journeymap-bukkit](https://github.com/TeamJM/journeymap-bukkit) (Spigot)
- [JourneyMap Server](https://www.curseforge.com/minecraft/mc-mods/journeymap-server) (Spigot)
- [Minimap server](https://github.com/Ewpratten/MinimapServer) (Spigot)
