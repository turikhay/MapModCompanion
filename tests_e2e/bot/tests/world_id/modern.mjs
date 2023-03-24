import { Client } from "minecraft-protocol";
import test from "./common.mjs";

export default {
  groups: ["voxelmap", "journeymap"],
  test: (/** @type Client */ client, /** @type number */ protocolVersion) =>
    protocolVersion > 393 // >= 1.13
      ? test(client, "worldinfo:world_id", {
          requestPadding: 1,
          responsePadding: 1,
        })
      : undefined,
};
