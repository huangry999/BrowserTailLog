import * as Types from '../constant/ActionTypes'
import { history } from '../Root'

export const intoDir = (dir) => ({ type: Types.INTO_DIR, dir, })
export const updateFileList = (files = [], dir, rollback) => ({
  type: Types.RESP_LIST_FILE,
  dir,
  rollback,
  files,
})

let configs = { windowSize: 100 }
export const init = () => {
  const uuidv4 = require('uuid/v4');
  const sessionId = uuidv4();
  return { type: Types.INIT, sessionId }
}
export const getInit = (c = {}) => {
  configs = c;
  return { type: Types.RESP_INIT, configs: c }
}

export const openLog = (key) => {
  window.open('/#/log/' + key);
  //history.push('/log/' + key);
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
export const getLogBetween = (path, skip, take = configs.windowSize) => {
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

export const gotoLogin = (message) => {
  history.push("/login");
  return { type: Types.GOTO_LOGIN, message }
}
export const doLogin = (id, password) => {
  return { type: Types.LOGIN, id, password }
}
export const loginSuccess = (token) => {
  history.goBack();
  return { type: Types.RESP_LOGIN_SUCCESS, token }
}