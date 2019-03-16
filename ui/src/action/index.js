import * as Types from '../constant/ActionTypes'
import { history } from '../Root'
import { store } from '../config/configureStore'

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
  return { type: Types.RESP_INIT, configs }
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

export const gotoLogin = (message) => {
  const needAuth = store.getState().configs.needAuth;
  if (!needAuth) {
    store.dispatch(doLogin('3%d8b'));
  } else {
    history.push("/");
  }
  return { type: Types.GOTO_LOGIN, message }
}
export const doLogin = (password) => {
  const crypto = require('crypto');
  const hash = crypto.createHash('sha256');
  hash.update("34)8e$" + password);
  const hashPw = hash.digest('hex');
  return { type: Types.LOGIN, password: hashPw }
}
export const loginSuccess = (token) => {
  store.dispatch(uploadToken(token));
  return { type: Types.RESP_LOGIN_SUCCESS, token }
}

export const gotoHost = () => {
  history.push("/host");
  return { type: Types.GOTO_HOST }
}
export const intoHost = (host) => {
  history.push("/log");
  return { type: Types.INTO_HOST, host }
}
export const fetchHost = () => ({ type: Types.FETCH_HOST })
export const setHost = (hosts) => ({ type: Types.RESP_FETCH_HOST, hosts })

export const uploadToken = (token) => ({ type: Types.UPLOAD_TOKEN, token })