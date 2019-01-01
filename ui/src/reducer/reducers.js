import { combineReducers } from 'redux'
import * as Types from '../constant/ActionTypes'
import Mode from '../protocol/Mode'
import ReceiveStatus from '../constant/ReceiveStatus'

/** 
 * state = {
 *    activeDir: string the directory in dashboard,
 *    files: array files detail in the active directory
 *    topWindow: array log contents
 *    bottomWindow: array log contents
 *    skip: number skip lines
 *    take: number take lines
 * }
 * 
*/

function files(state = [], action) {
  if (action.loading) {
    return state;
  }
  switch (action.type) {
    case Types.RESP_LIST_FILE:
      return action.files;
    default:
      return state;
  }
}

function dir(state = {}, action) {
  switch (action.type) {
    case Types.RESP_LIST_FILE:
      return { current: action.dir, rollback: action.rollback }
    default:
      return state;
  }
}

//TODO
function log(state = { path: '', receiveStatus: ReceiveStatus.RUNNING, data: [] }, action) {
  const copy = Object.assign({}, state);
  switch (action.type) {
    case Types.RESP_NEW_LOG_CONTENT:
      let key = 'data';
      if (state.receiveStatus === ReceiveStatus.PAUSE) {
        key = 'cache';
      }
      switch (action.mode) {
        case Mode.DELETE:
          copy[key] = [];
          if (key === 'cache') {
            copy[key].delete = true;
          }
          break;
        case Mode.CREATE:
          copy[key] = action.data;
          if (key === 'cache') {
            copy[key].cteate = true;
            delete copy[key].delete;
          }
          break;
        case Mode.MODIFY:
          if (copy[key]) {
            copy[key] = action.data.concat(copy[key]);
          } else {
            copy[key] = action.data;
          }
          break;
        default:
          return state;
      }
      return copy;
    case Types.RESP_GET_LOG_BETWEEN:
      copy.data = copy.data.concat(action.data);
      return copy;
    case Types.REVERSE_RECEIVE_STATUS:
      if (copy.receiveStatus === ReceiveStatus.RUNNING) {
        copy.receiveStatus = ReceiveStatus.PAUSE;
      } else {
        copy.receiveStatus = ReceiveStatus.RUNNING;
        if (copy.cache) {
          const cache = copy.cache;
          if (cache.delete) {
            copy.data = [];
          } else if (cache.cteate) {
            delete cache.cteate;
            copy.data = cache;
          } else {
            copy.data = cache.concat(copy.data);
          }
          delete copy.cache;
        }
      }
      return copy;
    case Types.OPEN_LOG:
      return { path: action.path, receiveStatus: ReceiveStatus.RUNNING, data: [] }
    default:
      return state;
  }
}

const rootReducer = combineReducers({
  files,
  dir,
  log,
})

export default rootReducer