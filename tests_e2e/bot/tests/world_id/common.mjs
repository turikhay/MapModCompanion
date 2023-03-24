import { Client } from "minecraft-protocol";

/**
 * @typedef {Object} Options
 * @property {number} requestPadding
 * @property {number} responsePadding
 */

export default function test(
  /** @type Client */ client,
  /** @type string */ channel,
  /** @type Options */ options
) {
  const { requestPadding, responsePadding } = options;

  const request = Buffer.alloc(requestPadding + 1);
  request[request.byteLength - 1] = 42;

  const responseBytes = Buffer.from("1337", "utf8");
  const response = Buffer.alloc(responsePadding + 2 + responseBytes.byteLength);
  response[responsePadding] = 42;
  response[responsePadding + 1] = responseBytes.byteLength;
  responseBytes.copy(response, responsePadding + 2);

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
        reject(`unexpected response`);
      }
    });
  });
}

const MAGIC_NUMBER = 42;

function readResponse(/** @type Buffer */ buffer, /** @type number */ offset) {
  let read = 0,
    idLength = Number.NaN;
  for (let i = offset; i < buffer.byteLength; i++) {
    const byte = buffer.readInt8(i);
    read++;
    if (byte == MAGIC_NUMBER) {
      idLength = buffer.readInt8(i + 1);
      break;
    }
  }
  if (Number.isNaN(idLength)) {
    throw "missing magic number";
  }
  const value = Buffer.alloc(read + idLength + 1);
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
