# Companion for map mods

<p>
  <a href="https://github.com/turikhay/MapModCompanion/blob/main/LICENSE.txt">
    <img src="https://img.shields.io/github/license/turikhay/MapModCompanion">
  </a>
  <a href="https://github.com/turikhay/MapModCompanion/actions/workflows/e2e_notable.yml">
    <img src="https://github.com/turikhay/MapModCompanion/actions/workflows/e2e_notable.yml/badge.svg" />
  </a>
<!-- platform.start -->
  <a href="https://www.spigotmc.org/resources/mapmodcompanion.105128/">
    <img src="https://img.shields.io/spiget/downloads/105128?label=Spigot%20%28downloads%29">
  </a>
  <a href="https://modrinth.com/plugin/modmapcompanion">
    <img src="https://img.shields.io/modrinth/dt/UO7aDcrF?label=Modrinth%20%28downloads%29" />
  </a>
  <a href="https://hangar.papermc.io/turikhay/MapModCompanion">
    <img src="https://img.shields.io/hangar/dt/MapModCompanion?label=Hangar%20(downloads)" />
  </a>
  <a href="https://www.curseforge.com/minecraft/bukkit-plugins/mapmodcompanion">
    <img src="https://cf.way2muchnoise.eu/full_674380_downloads.svg">
  </a>
<!-- platform.end -->
  <a href="https://www.buymeacoffee.com/turikhay">
    <img src="https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png" height="20px">
  </a>
</p>

<!-- platform.start -->
<img
  align="right"
  width="200"
  height="200"
  src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/allaylogo3_1000_10.png"
  alt="Allay from Minecraft holding a compass and waving with their other hand at the viewer"
/>
<!-- platform.end -->

**With this plugin your minimap will never be confused which world you're in. [A more in-depth explanation can be found in the wiki](https://github.com/turikhay/MapModCompanion/wiki/How-it-works).**

<details><summary>How it should look like</summary>

| Mod | Screenshot |
| ----|------------|
| Xaero's World Map | <img src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/2023-03-28_00.32.04_1.png" width="200" alt="Screenshot of Xaero's WorldMap menu" /> |
| VoxelMap | <img src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/2023-03-28_00.34.50_1.png" width="200" alt="Screenshot of the game with a minimap on the top-right corner" /> <img src="https://raw.githubusercontent.com/turikhay/MapModCompanion-design/main/2023-03-28_00.35.04_1.png" width="200" alt="Screenshot of a map" /> |
| Xaero's Minimap | See Xaero's WorldMap |
| JourneyMap | It just works 😄 |

</details> 

Companion plugin for
[Xaero's Minimap]
(and their [World Map][Xaero's World Map]),
[JourneyMap] and
VoxelMap (both [old][VoxelMap (old)] and [updated][VoxelMap-Updated]).
Provides a way for these mods to identify worlds on BungeeCord/Velocity servers.

It's recommended to install this plugin on a fresh server, otherwise **existing map data**
(waypoints, map cache, etc.) **may no longer be visible to some players**. Fortunately,
[there are ways to restore it](https://github.com/turikhay/MapModCompanion/wiki/Restore-map-data).
It's worth mentioning that the plugin doesn't affect in-game progress.

This plugin was inspired by @kosma's [worldnamepacket],
which supported Velocity, Fabric and Spigot at the time of writing.

If you have any questions, please [join my Discord][Discord].

[![](https://bstats.org/signatures/bukkit/MapModCompanion.svg)](https://bstats.org/plugin/bukkit/MapModCompanion/16539 "MapModCompanion on bStats")

## Support table
| Mod                                                                                | Oldest version             | Latest version                                               | Status      |
|------------------------------------------------------------------------------------|----------------------------|--------------------------------------------------------------|-------------|
| [Xaero's Minimap]     | v20.20.0 / Minecraft 1.8.9 | v24.7.1 / Minecraft 1.21.4                                   | ✅ Supported |
| [Xaero's World Map] | v1.10.0 / Minecraft 1.8.9  | v1.39.2 / Minecraft 1.21.4                                   | ✅ Supported<sup class="reference">[[1]](https://github.com/turikhay/MapModCompanion/issues/62)</sup> |
| [JourneyMap]              | v5.7.1 / Minecraft 1.16.5  | v6.0.0 / Minecraft 1.21.4                               | ✅ Supported |
| VoxelMap                                                                           | [v1.7.10][VoxelMap (old)] / Minecraft 1.8    | [v1.14.7][VoxelMap-Updated] / Minecraft 1.21.4 | ✅ Supported<sup class="reference">[[2]](https://github.com/turikhay/MapModCompanion/issues/8)</sup> |

[Folia](https://papermc.io/software/folia) is supported, but isn't tested thoroughly. Please report if the support is broken.

## Installation

ℹ️ Plugin must be installed on every downstream (backend) server in your network. Simply installing it on the proxy side (BungeeCord/Velocity) isn't enough. To ensure compatibility, you need to install the plugin on both the proxy server (BungeeCord/Velocity) and each of the backend servers (Spigot/Paper).

1. Download the latest release
2. Put each file into the corresponding plugins folder
3. That's it. No configuration is required. You can restart your servers now.

## Configuration
The configuration file is stored at `plugins/MapModCompanion/config.yml` for both Spigot and BungeeCord.
Velocity uses `plugins/mapmodcompanion/config.toml`.

The configuration file reloads automatically if it's modified.

<!-- platform.start -->
## Alternatives
- If you're running Forge or Fabric server, just install the map mod on your server: this will unlock all its
  features.
- [worldnamepacket] (Velocity, Fabric, Spigot)
- [journeymap-bukkit](https://github.com/TeamJM/journeymap-bukkit) (Spigot)
- [JourneyMap Server](https://www.curseforge.com/minecraft/mc-mods/journeymap-server) (Spigot)
<!-- platform.end -->

[Discord]: https://discord.gg/H9ACHEqBrg
[Xaero's Minimap]: https://modrinth.com/mod/xaeros-minimap
[Xaero's World Map]: https://modrinth.com/mod/xaeros-world-map
[JourneyMap]: https://modrinth.com/mod/journeymap
[VoxelMap (old)]: https://www.curseforge.com/minecraft/mc-mods/voxelmap
[VoxelMap-Updated]: https://modrinth.com/mod/voxelmap-updated
[worldnamepacket]: https://github.com/kosma/worldnamepacket
