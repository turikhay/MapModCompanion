import { Client } from "minecraft-protocol";

const expectedResponseBuffer = Buffer.from("1337");
export const expectedResponseBytes = [
  expectedResponseBuffer.byteLength,
  ...expectedResponseBuffer,
];

/**
 * @typedef {Object} Options
 * @property {number[]} request
 * @property {number[]} response
 */

export default function test(
  /** @type Client */ client,
  /** @type string */ channel,
  /** @type Options */ options
) {
  const request = Buffer.from(options.request);
  const response = Buffer.from(options.response);

  client.on("login", function () {
    client.registerChannel(channel, ["restBuffer", []], true);
    client.writeChannel(channel, request);
  });

  return new Promise((resolve, reject) => {
    // validate response
    client.on(channel, (/** @type Buffer */ buffer) => {
      const isValid = buffer.equals(response);
      if (isValid) {
        resolve();
      } else {
        reject(
          `unexpected response: ${[...buffer]}; expected: ${[...response]}`
        );
      }
    });
  });
}
