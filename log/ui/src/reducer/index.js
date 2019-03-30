import * as Types from '../constant/ActionTypes'
import Mode from '../protocol/Mode'
import ReceiveStatus from '../constant/ReceiveStatus'
import ViewMode from '../constant/ViewMode'
import FileType, { valueOf } from '../constant/FileType'

export function files(state = [], action) {
  if (action.loading) {
    return state;
  }
  switch (action.type) {
    case Types.RESP_LIST_FILE:
      return action.files;
    case Types.INTO_HOST:
      return [];
    default:
      return state;
  }
}

const defDirState = {
  current: undefined,
  rollback: {
    inHostPath: true,
    inRootPath: false,
    rollBackPath: undefined
  }
};
export function dir(state = defDirState, action) {
  switch (action.type) {
    case Types.RESP_LIST_FILE:
      return { current: action.dir, rollback: action.rollback }
    case Types.INTO_HOST:
      return defDirState;
    default:
      return state;
  }
}

const initLogStastus = {
  receiveStatus: ReceiveStatus.RUNNING,
  lockBottom: true,
  loading: false,
  mode: ViewMode.SCAN,
}
function logStatus(state = initLogStastus, action) {
  switch (action.type) {
    case Types.SET_RECEIVE_STATUS:
      return Object.assign({}, state, { receiveStatus: action.status });
    case Types.SET_LOCK_BOTTOM:
      return Object.assign({}, state, { lockBottom: action.isLock });
    case Types.LOAD_LOG:
      return Object.assign({}, initLogStastus, { loading: true });
    case Types.GET_LOG_BETWEEN:
      return Object.assign({}, state, { loading: true });
    case Types.RESP_GET_LOG_BETWEEN:
      return Object.assign({}, state, { loading: false });
    case Types.FIND_BY_LINE:
      return Object.assign({}, state, { mode: ViewMode.FIND_LINE, lockBottom: false, receiveStatus: ReceiveStatus.PAUSE });
    default:
      return state;
  }
}

function cache(state = { data: [], replace: false }, action, logStatus) {
  if (logStatus.receiveStatus === ReceiveStatus.RUNNING) {
    return state;
  }
  switch (action.type) {
    case Types.RESP_NEW_LOG_CONTENT:
      switch (action.mode) {
        case Mode.DELETE:
          return { data: [], replace: true }
        case Mode.CREATE:
          return { data: action.data, replace: true }
        case Mode.MODIFY:
          return Object.assign({}, state, { data: state.data.concat(action.data) });
        default:
          return state;
      }
    default:
      return state;
  }
}

export function log(state = { data: [] }, action) {
  const copy = Object.assign({}, state);
  copy.logStatus = logStatus(state.logStatus, action);
  copy.cache = cache(state.cache, action, copy.logStatus);

  switch (action.type) {
    case Types.RESP_NEW_LOG_CONTENT:
      if (copy.logStatus.receiveStatus === ReceiveStatus.RUNNING) {
        switch (action.mode) {
          case Mode.DELETE:
            copy.data = [];
            break;
          case Mode.CREATE:
            copy.data = action.data;
            break;
          case Mode.MODIFY:
            copy.data = copy.data.concat(action.data);
            break;
          default:
            return copy;
        }
      }
      return copy;
    case Types.RESP_GET_LOG_BETWEEN:
      if (action.data.length === 0) {
        return copy;
      }
      if (copy.data.length === 0) {
        copy.data = action.data;
      } else if (copy.data[0].lineNo - 1 === action.data.slice(-1)[0].lineNo) {
        copy.data = action.data.concat(copy.data);
      } else if (copy.data.slice(-1)[0].lineNo + 1 === action.data[0].lineNo) {
        copy.data = copy.data.concat(action.data);
      }
      return copy;
    case Types.SET_RECEIVE_STATUS:
      if (copy.logStatus.receiveStatus === ReceiveStatus.RUNNING) {
        const cache = copy.cache;
        if (cache.replace) {
          copy.data = cache.data;
        } else {
          /**
           * just copy cache data start with the last line of now data, 
           * cause RESP_GET_LOG_BETWEEN may update the tail content
           */
          const lastNo = Object.assign({}, copy.data.slice(-1)[0]).lineNo;
          if (lastNo) {
            const i = cache.data.findIndex(d => d.lineNo > lastNo);
            if (i !== -1) {
              copy.data = copy.data.concat(cache.data.slice(i));
            }
          } else {
            copy.data = copy.data.concat(cache.data);
          }
        }
        delete copy.cache;
      }
      return copy;
    case Types.REPLACE_LOG_CONTENT:
      copy.data = action.data;
      return copy;
    case Types.FIND_BY_LINE:
    case Types.LOAD_LOG:
      copy.data = [];
      return copy;
    default:
      return copy;
  }
}

export function configs(state = { windowSize: 100, shrinkThreshold: 100, needAuth: true }, action) {
  switch (action.type) {
    case Types.RESP_INIT:
      return Object.assign(state, action.configs);
    default:
      return state;
  }
}

export function tipInfo(state = {}, action) {
  switch (action.type) {
    case Types.GET_LOG_BETWEEN:
      return Object.assign(state, { loadingInfo: `Loading From ${action.skip + 1} To ${action.skip + action.take}` });
    case Types.LOAD_LOG:
      return Object.assign(state, { loadingInfo: `Loading...` });
    case Types.GOTO_LOGIN:
      return Object.assign(state, { loginTip: action.message });
    default:
      return state;
  }
}

export function system(state = {}, action) {
  switch (action.type) {
    case Types.RESP_LOGIN_SUCCESS:
      return Object.assign({}, state, { token: action.token });
    default:
      return state;
  }
}

export function openLogMap(state = {}, action) {
  switch (action.type) {
    case Types.RESP_LIST_FILE:
      const ns = Object.assign({}, state);
      action.files.forEach(f => {
        if (valueOf(f.type) === FileType.LOG) {
          ns[f.key] = f.path;
        }
      });
      return ns;
    default:
      return state;
  }
}

export function host(state = { hosts: [], currentHost: undefined }, action) {
  switch (action.type) {
    case Types.RESP_FETCH_HOST:
      return Object.assign({}, state, { hosts: action.hosts });
    case Types.INTO_HOST:
      return Object.assign({}, state, { currentHost: action.host });
    default:
      return state;
  }
}