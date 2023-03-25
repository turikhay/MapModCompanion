import { Client } from "minecraft-protocol";
import test from "./common.mjs";

export default {
  groups: ["xaeroworldmap"],
  test: (/** @type Client */ client) => test(client, "xaeroworldmap:main"),
};
