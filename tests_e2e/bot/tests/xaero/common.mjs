import { Client } from "minecraft-protocol";

export default function test(
  /** @type Client */ client,
  /** @type string */ channel
) {
  // register channel
  client.once("login", () => {
    client.registerChannel(channel, undefined);
  });

  return new Promise((resolve, reject) => {
    // validate incoming packets in our channel
    client.on(channel, (/** @type Buffer */ buffer) => {
      const isExpected = buffer.equals(Buffer.from([0, 0, 0, 5, 57]));
      if (isExpected) {
        resolve();
      } else {
        reject(`unexpected data: ${buffer}`);
      }
    });
  });
}
