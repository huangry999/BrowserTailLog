import * as Types from '../constant/ActionTypes'

export const intoDir = (dir) => ({
  type: Types.INTO_DIR,
  dir,
})
export const updateFileList = (files = [], dir, rollback) => ({
  type: Types.RESP_LIST_FILE,
  dir,
  rollback,
  files,
})

export const init = () => ({
  type: Types.INIT,
})
export const getInit = (files = []) => ({
  type: Types.RESP_INIT,
  files,
})

export const openLog = path => {
  return { type: Types.OPEN_LOG, path }
}
export const getNewLogContent = (path, data = [], mode) => {
  return {
    type: Types.RESP_NEW_LOG_CONTENT,
    path,
    data,
    mode,
  }
}

let getBetweenLock = false;
export const getLogBetween = (path, from, take) => {
  if (getBetweenLock) {
    return { type: Types.IGNORE }
  }
  getBetweenLock = true;
  return {
    type: Types.GET_LOG_BETWEEN,
    path,
    from,
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

export const reverseReceiveStatus = () => {
  return { type: Types.REVERSE_RECEIVE_STATUS }
}