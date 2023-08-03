import { Client } from "minecraft-protocol";
import test, { expectedResponseBytes } from "./common.mjs";

export default {
  groups: ["voxelmap"],
  test: (/** @type Client */ client, /** @type number */ protocolVersion) =>
    protocolVersion >= 761
      ? // 1.19.3+ uses modern protocol
        undefined
      : test(
          client,
          // use non-prefixed version when < 1.13
          protocolVersion <= 340 ? "world_id" : "worldinfo:world_id",
          {
            request: [0, 0, 0, 42],
            response: [42, ...expectedResponseBytes],
          },
        ),
};
