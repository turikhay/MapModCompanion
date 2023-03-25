import { Client } from "minecraft-protocol";
import test, { expectedResponseBytes } from "./common.mjs";

export default {
  groups: ["voxelmap"],
  test: (/** @type Client */ client, /** @type number */ protocolVersion) =>
    protocolVersion < 340 || protocolVersion > 754 // Forge VoxelMap 1.12.2 - 1.16.5
      ? undefined
      : test(
          client,
          // non-prefixed channel on <= 1.12.2
          protocolVersion <= 340 ? "world_id" : "worldinfo:world_id",
          {
            request: protocolVersion == 340 ? [0, 0] : [0, 42, 0],
            response:
              protocolVersion <= 753
                ? [0, ...expectedResponseBytes]
                : [0, 42, ...expectedResponseBytes],
          }
        ),
};
