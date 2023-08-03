import { Client } from "minecraft-protocol";

const expectedResponseBuffer = Buffer.alloc(5);
expectedResponseBuffer.writeInt32BE(1337, 1);

export default function test(
  /** @type Client */ client,
  /** @type string */ channel,
) {
  // register channel
  client.once("login", () => {
    client.registerChannel(channel, ["restBuffer", []], true);
  });

  return new Promise((resolve, reject) => {
    // validate incoming packets in our channel
    client.on(channel, (/** @type Buffer */ buffer) => {
      const isExpected = buffer.equals(expectedResponseBuffer);
      if (isExpected) {
        resolve();
      } else {
        reject(`unexpected data: ${buffer}`);
      }
    });
  });
}
