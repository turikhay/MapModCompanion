import { Client } from "minecraft-protocol";
import test from "./common.mjs";

export default {
  groups: ["xaerominimap"],
  test: (/** @type Client */ client) => test(client, "xaerominimap:main"),
};
