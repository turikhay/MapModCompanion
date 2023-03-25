import { createClient } from "minecraft-protocol";

const RETRY_SECONDS = 3;
const RETRY_MAX_COUNT = 60;
const skipped = new Set();

export default async function startTestClient(clientOptions) {
  const { name } = clientOptions;
  let connectRetries = 0;
  let tryAgain = false;
  do {
    try {
      await performTests(clientOptions);
    } catch (e) {
      if (e === "connect error" && ++connectRetries < RETRY_MAX_COUNT) {
        // console.log(
        //   `😒 ${name} failed to connect to the server. Retrying in ${RETRY_SECONDS} seconds...`
        // );
        await new Promise((resolve) =>
          setTimeout(resolve, RETRY_SECONDS * 1000)
        );
        tryAgain = true;
        continue;
      }
      console.error(`⚠️  ${name} error:`, e);
      throw e;
    }
    return;
  } while (tryAgain);
}

function performTests({
  name: clientName,
  host,
  port,
  username,
  version,
  protocolVersion,
  tests,
}) {
  const client = createClient({
    host,
    port,
    username,
    version,
  });
  const results = tests.map(({ name, test }) => ({
    name,
    result: test.test ? test.test(client, protocolVersion) : undefined,
  }));
  let allCompleted = false,
    connected = false;
  return new Promise((resolve, reject) => {
    client.on("error", (e) => {
      if (connected) {
        console.warn(`Error in ${clientName}: ${e}`);
        reject(e);
      }
    });
    client.once("end", () => {
      if (allCompleted) {
        return;
      }
      if (connected) {
        reject(`disconnected while running tests`);
      } else {
        reject(`connect error`);
      }
    });
    const promises = results.map(async ({ name: testName, result }) => {
      if (!result) {
        if (!skipped.has(testName)) {
          skipped.add(testName);
          console.log(`↘️  Skipped: ${testName}`);
        }
        return;
      }
      // console.log(`⏳ ${clientName} waits for ${name}`);
      try {
        await result;
      } catch (e) {
        console.warn(`${clientName} 💀  failed: ${testName}: ${e}`);
        reject(e);
        return;
      }
      console.log(`${clientName} ✅  OK: ${testName}`);
    });
    Promise.all(promises)
      .then(() => resolve())
      .catch((e) => reject(e));
    client.once("login", () => {
      console.log(`✅ ${clientName} has connected to the server`);
      connected = true;
      setTimeout(() => {
        if (!allCompleted) {
          reject("timed out");
        }
      }, 15000);
    });
  })
    .then(() => {
      allCompleted = true;
    })
    .finally(() => client.end());
}
