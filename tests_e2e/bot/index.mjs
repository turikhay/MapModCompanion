import argsParser from "args-parser";
import dataProvider from "minecraft-data";

import xaeroMinimapTest from "./tests/xaero/minimap.mjs";
import xaeroWorldMapTest from "./tests/xaero/worldmap.mjs";
import voxelMapLegacy from "./tests/world_id/legacy.mjs";
import voxelMapForge from "./tests/world_id/forge.mjs";
import worldIdModern from "./tests/world_id/modern.mjs";
import startTestClient from "./testClient.mjs";

const tests = [
  { name: "Xaero's Minimap", test: xaeroMinimapTest },
  { name: "Xaero's Worldmap", test: xaeroWorldMapTest },
  { name: "VoxelMap (legacy)", test: voxelMapLegacy },
  { name: "Forge VoxelMap", test: voxelMapForge },
  { name: "worldinfo:world_id (modern)", test: worldIdModern },
];

const args = argsParser(process.argv);

const version =
  args.version ??
  process.env.BOT_VERSION ??
  (() => {
    throw new Error("version not defined");
  })();

const data = dataProvider(version);

if (!data) {
  throw `${version} not supported or unknown`;
}

const clientOptions = {
  host: args.host ?? process.env.BOT_HOST ?? "127.0.0.1",
  port: args.port ?? process.env.BOT_PORT ?? 25565,
  version,
  protocolVersion: data.version.version,
};

const clientGroups = [];

tests.forEach((tf) => {
  for (const testsArray of clientGroups) {
    const hasConflict = testsArray.some((tf1) =>
      tf1.test.groups.some((group) => tf.test.groups.includes(group))
    );
    if (hasConflict) {
      continue;
    }
    testsArray.push(tf);
    return;
  }
  clientGroups.push([tf]);
});

console.log(`⏳ Testing MapModCompanion on Minecraft ${version}`);

const results = clientGroups.map((group, index) =>
  startTestClient({
    ...clientOptions,
    name: `Client#${index}`,
    username: `test${index}`,
    tests: group,
  })
);

let success = true;
try {
  await Promise.all(results);
} catch (e) {
  success = false;
}

if (!success) {
  console.warn(`😢  There were failing tests`);
  process.exit(1);
}

console.log(`🎉 Success`);
process.exit(0);
