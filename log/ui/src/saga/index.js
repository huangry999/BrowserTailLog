import { takeEvery, takeLatest, put, call, all } from 'redux-saga/effects'
import * as types from '../constant/ActionTypes'
import { encode } from '../protocol/ProtocolUtil'
import Request from '../protocol/Request'
import { getLogBetween, setHost, setInit, loginSuccess, gotoHost, gotoLogin, doLogin } from '../action'
import * as Api from './fetchData'
import { history } from '../Root'

function* websocketWatch(socket, store) {
  yield takeLatest(types.INTO_DIR, (action) => {
    const f = encode(Request.CHANGE_DIR, { hostName: store.getState().host.currentHost.name, path: action.dir });
    socket.send(f);
  })
  yield takeLatest(types.LOAD_LOG, (action) => {
    const f = encode(Request.SUBSCRIBE, { hostName: store.getState().host.currentHost.name, path: action.path });
    socket.send(f);
  })
  yield takeEvery(types.GET_LOG_BETWEEN, (action) => {
    const f = encode(Request.REQUEST_BETWEEN, { hostName: store.getState().host.currentHost.name, path: action.path, skip: action.skip, take: action.take });
    socket.send(f);
  })
  yield takeEvery(types.RESP_LOGIN_SUCCESS, (action) => {
    const f = encode(Request.TOKEN, { token: action.token });
    socket.send(f);
    if (action.next) {
      put(action.next());
    }
  })
  yield takeEvery(types.INIT, () => {
    const { system } = store.getState();
    if (system && system.token) {
      const f = encode(Request.TOKEN, { token: system.token });
      socket.send(f);
    }
  })
}

function* redirectWatch(store) {
  yield takeEvery(types.FIND_BY_LINE, (action) => {
    put(getLogBetween(action.path, action.lineNo - 1, action.take));
  });
  yield takeEvery(types.RESP_LOGIN_SUCCESS, () => put(gotoHost()));
  yield takeEvery(types.GOTO_LOGIN, (action) => {
    const needAuth = store.getState().configs.needAuth;
    if (!needAuth) {
      store.dispatch(doLogin('3%d8b', action.next));
    } else {
      history.push("/");
    }
  });
  yield takeEvery(types.GOTO_HOST, () => history.push("/host"));
  yield takeEvery(types.INTO_HOST, (action) => history.push(`/${action.host.name}/log`));
  yield takeEvery(types.OPEN_LOG, (action) => {
    const hostName = store.getState().host.currentHost.name;
    window.open(`/#/${hostName}/read/${action.key}`);
    //history.push(`/${hostName}/read/${action.key}`)
  });
}

function* httpWatch() {
  yield takeLatest(types.LOGIN, login);
  yield takeLatest(types.FETCH_HOST, getHost);
  yield takeLatest(types.INIT, getInit);
}
function* getHost() {
  try {
    const response = yield call(Api.fetchHosts);
    yield put(setHost(response));
  } catch (e) {
    yield put(gotoLogin('', gotoHost));
    return;
  }
}
function* getInit() {
  const response = yield call(Api.fetchInit);
  yield put(setInit(response));
}
function* login(action) {
  try {
    const response = yield call(Api.login, action.password);
    yield put(loginSuccess(response.access_token, action.next));
  } catch (e) {
    yield put(gotoLogin('Login account or password error', action.next));
    return;
  }
}

export default function* rootSage(params) {
  yield all([
    websocketWatch(params.socket, params.store),
    httpWatch(),
    redirectWatch(params.store),
  ]);
}
