import { Client } from "minecraft-protocol";
import test from "./common.mjs";

export default {
  groups: ["voxelmap", "journeymap"],
  test: (/** @type Client */ client, /** @type number */ protocolVersion) =>
    protocolVersion >= 754 // >= 1.16.4
      ? test(client, "worldinfo:world_id", {
        request: [0, 42, 0],
        response: [42, 1, 49],
      })
      : undefined,
};
