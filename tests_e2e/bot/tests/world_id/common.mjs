import { Client } from "minecraft-protocol";

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

  client.registerChannel(channel, [readResponse, writeRequest, sizeOfRequest]);

  client.on("login", function () {
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

const MAGIC_NUMBER = 42;

function readResponse(/** @type Buffer */ buffer, /** @type number */ offset) {
  let read = 0,
    idLength = Number.NaN,
    lastByte;
  for (let i = offset; i < buffer.byteLength; i++) {
    lastByte = buffer.readInt8(i);
    read++;
    if (lastByte != 0) {
      if (lastByte == MAGIC_NUMBER) {
        read++;
        idLength = buffer.readInt8(i + 1);
      } else {
        idLength = lastByte;
      }
      break;
    }
  }
  const value = Buffer.alloc(read + idLength);
  buffer.copy(value, 0, offset);
  return { value, size: value.byteLength };
}

function writeRequest(
  /** @type Buffer */ value,
  /** @type Buffer */ buffer,
  /** @type number */ offset
) {
  value.copy(buffer, offset);
  return offset + value.byteLength;
}

function sizeOfRequest(/** @type Buffer */ value) {
  return value.byteLength;
}
