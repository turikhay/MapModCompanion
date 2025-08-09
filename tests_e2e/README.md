# E2E tests for MapModCompanion

⚠️ In order to run E2E tests you'll need at least 2 GiB of _available_ RAM. Check it by running:

```shell
$ free -h
```

## Why?

I wanted to verify if the plugin actually works on all Minecraft versions we claim it supports. Setting up 30+ Paper, BungeeCord and Velocity instances manually was just too much work for me.

## How?

Thanks to [@itzg](https://github.com/itzg), who probably spent countless hours on [Docker image for hosting Minecraft server](https://github.com/itzg/docker-minecraft-server), it was easy. We just spin up a test server using Docker Compose and log into the game. The bot (which is built using [node-minecraft-protocol](https://github.com/PrismarineJS/node-minecraft-protocol)) only needs to join the server and listen to specific plugin channels.

Automatic bot test is not _that_ useful because it doesn't behave like real Minecraft client. Now I can easily debug different combinations of Minecraft server versions and proxies.

## Let me try

```shell
$ DEBUG=1 ./run.sh <proxy> <version> <command>
```

Proxy can be one of the following:
* `bungeecord`
* `waterfall`
* `velocity`

Version can be `1.8.9`, `1.12.2`, `1.16.5`, `1.19.3` etc.

Command:
* `test` – perform automatic (E2E) tests
* `manual` – spin up specified server and proxy
* * Use env `JAVA_DEBUG=1` to enable Java debugging. Local port `9010` goes for the proxy, `9011` for the server. Example: `JAVA_DEBUG=1 ./run.sh waterfall 1.16.5 manual`
* * Use env `BLUE=1` to enable second server (e.g. to debug map persistence). You'll be able to switch between servers with `/server red/blue`.
* * Use Env `SERVER_TYPE=folia` if you want to use Folia as a backend Minecraft server. Note that the script will just exit if there is no Folia support for the selected version.
