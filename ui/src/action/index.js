import * as Types from '../constant/ActionTypes'
import { initWebsite } from '../index'

export const intoDir = (dir) => ({ type: Types.INTO_DIR, dir, })
export const updateFileList = (files = [], dir, rollback) => ({
  type: Types.RESP_LIST_FILE,
  dir,
  rollback,
  files,
})

export const init = () => {
  return { type: Types.INIT }
}
export const setInit = (configs) => {
  initWebsite();
  return { type: Types.RESP_INIT, configs }
}

export const openLog = (key) => {
  return { type: Types.OPEN_LOG, key }
}
export const loadLog = path => ({ type: Types.LOAD_LOG, path });
export const getNewLogContent = (path, data = [], mode) => ({
  type: Types.RESP_NEW_LOG_CONTENT,
  path,
  data,
  mode,
})

let getBetweenLock = false;
export const getLogBetween = (path, skip, take) => {
  if (getBetweenLock) {
    return { type: Types.IGNORE }
  }
  getBetweenLock = true;
  return {
    type: Types.GET_LOG_BETWEEN,
    path,
    skip,
    take,
  }
}
export const logContentBetween = (path, data = []) => {
  getBetweenLock = false;
  return {
    type: Types.RESP_GET_LOG_BETWEEN,
    path,
    data,
  }
}

export const setReceiveStatus = (status) => ({ type: Types.SET_RECEIVE_STATUS, status })

export const replaceLogContent = (data = []) => ({ type: Types.REPLACE_LOG_CONTENT, data })

export const setLockBottom = (isLock) => ({ type: Types.SET_LOCK_BOTTOM, isLock })

export const findByLine = (path, lineNo, take) => ({ type: Types.FIND_BY_LINE, path, lineNo, take })

export const gotoLogin = (message, next) => {
  return { type: Types.GOTO_LOGIN, message, next }
}
export const doLogin = (password, next) => {
  const crypto = require('crypto');
  const hash = crypto.createHash('sha256');
  hash.update("34)8e$" + password);
  const hashPw = hash.digest('hex');
  return { type: Types.LOGIN, password: hashPw, next }
}
export const loginSuccess = (token, next) => {
  return { type: Types.RESP_LOGIN_SUCCESS, token, next }
}

export const gotoHost = () => {
  return { type: Types.GOTO_HOST }
}
export const intoHost = (host) => {
  return { type: Types.INTO_HOST, host }
}
export const fetchHost = () => ({ type: Types.FETCH_HOST })
export const setHost = (hosts) => ({ type: Types.RESP_FETCH_HOST, hosts })