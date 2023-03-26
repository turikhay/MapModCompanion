# E2E tests for MapModCompanion

## Why?

I wanted to verify if the plugin actually works on all Minecraft versions we claim it supports. Setting up 30+ Paper, Bungeecord and Velocity instances manually was just too much work for me.

## How?

Thanks to [@itzg](https://github.com/itzg) who probably spent countless hours on [Docker image for hosting Minecraft server](https://github.com/itzg/docker-minecraft-server) it was easy. We just spin up a test server using Docker Compose and log into the game. The bot (which is built using [node-minecraft-protocol](https://github.com/PrismarineJS/node-minecraft-protocol)) only needs to join the server and listen to specific plugin channels.

But automatic bot test is not _that_ useful. It doesn't behave like real Minecraft client.

More importantly, now I can easily debug different combinations of Minecraft server versions and proxies.

## Let me try

```shell
$ ./run.sh <proxy> <version> <command>
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
* `build` – build Docker images
* `cleanup` – calls `docker compose down`
* `convert` – calls `docker compose convert`
