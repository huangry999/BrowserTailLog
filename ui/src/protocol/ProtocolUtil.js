import Request from './Request'
import { valueOf as respondValOf } from './Respond'
import { valueOf as modeValOf } from './Mode'
import Mode from './Mode'
import logger from '../log'
import { blobToArrayBuffer } from 'blob-util';

const frameHeadSize = 10;

/**
 * decode log protocol frame
 * 
 * @param {Blob} blob data 
 * @returns promise of Object{respond, data}
 */
export function decode(blob) {
  return blobToArrayBuffer(blob)
    .then(b => {
      const buffer = Buffer.from(b);
      const respond = respondValOf(buffer.readUInt8(6));
      const mode = modeValOf(buffer.readUInt8(7));
      const data = JSON.parse(buffer.toString('utf8', 10));
      logger.debug(`decord respond : ${respond}, data: ${data}, mode: ${mode}`)
      return { respond, data, mode }
    })
    .catch(err => logger.error(err.message));
}

/**
 * encode to log protocol
 * 
 * @param {Request} request the request
 * @param {Object} data json body to send 
 */
export function encode(request = Request.INIT, body = {}) {
  const data = JSON.stringify(body);
  return encode0(request, data);
}

/**
 * encode to log protocol
 * 
 * @param {Request} request the request
 * @param {Object} data string body to send 
 */
export function encode0(request = Request.INIT, data = "") {
  const dataBuffer = Buffer.from(data, 'utf8');
  const size = frameHeadSize + dataBuffer.length;
  const frame = Buffer.allocUnsafe(size);
  frame.writeUInt16BE(0x112c, 0);
  frame.writeUInt16BE(size, 2);
  frame.writeUInt8(0x11, 4);
  frame.writeUInt8(0x2, 5);
  frame.writeUInt8(request.code, 6);
  frame.writeUInt8(Mode.NONE.code, 7);
  frame.writeUInt16BE(0, 8);
  const headByts = Uint8Array.from(frame.slice(0, 10));
  const checksum = calchecksum(headByts);
  frame.writeUInt16BE(checksum, 8);
  frame.write(data, 10, 'utf8');
  return frame;
}

function calchecksum(byteArray = []) {
  let c = 0;
  for (let i = 0; i < byteArray.length; i += 2) {
    const h = byteArray[i];
    const l = byteArray[i + 1];
    c += (h << 8) + l
  }
  while (c >>> 16 !== 0) {
    c = (c >>> 16) + (c & 65535);
  }
  return ~c & 65535;
}

export { frameHeadSize };