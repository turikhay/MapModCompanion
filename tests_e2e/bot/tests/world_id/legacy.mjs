import { Client } from "minecraft-protocol";
import test from "./common.mjs";

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
            requestPadding: 3,
            responsePadding: 0,
          }
        ),
};
