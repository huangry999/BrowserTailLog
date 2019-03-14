import { takeEvery, takeLatest, put, call, all } from 'redux-saga/effects'
import * as types from '../constant/ActionTypes'
import { encode } from '../protocol/ProtocolUtil'
import Request from '../protocol/Request'
import { getLogBetween, setHost, setInit, loginSuccess } from '../action'
import * as Api from './fetchData'

function* websocketWatch(socket) {
  yield takeLatest(types.INTO_DIR, (action) => {
    const f = encode(Request.CHANGE_DIR, { path: action.dir });
    socket.send(f);
  })
  yield takeLatest(types.LOAD_LOG, (action) => {
    const f = encode(Request.SUBSCRIBE, { path: action.path });
    socket.send(f);
  })
  yield takeEvery(types.GET_LOG_BETWEEN, (action) => {
    const f = encode(Request.REQUEST_BETWEEN, { path: action.path, skip: action.skip, take: action.take });
    socket.send(f);
  })
  // yield takeEvery(types.LOGIN, (action) => {
  //   const f = encode(Request.LOGIN, { id: action.id, password: action.password });
  //   socket.send(f);
  // })
}

function* redirectWatch() {
  yield takeEvery(types.FIND_BY_LINE, (action) => {
    put(getLogBetween(action.path, action.lineNo - 1, action.take));
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
    //TODO yield put(fetchFailed(e));
    return;
  }
}
function* getInit() {
  try {
    const response = yield call(Api.fetchInit);
    yield put(setInit(response));
  } catch (e) {
    //TODO yield put(fetchFailed(e));
    return;
  }
}
function* login(action) {
  try {
    const response = yield call(Api.login, action.password);
    yield put(loginSuccess(response.access_token));
  } catch (e) {
    //TODO yield put(fetchFailed(e));
    return;
  }
}

export default function* rootSage(params) {
  yield all([
    websocketWatch(params.socket),
    httpWatch(),
    redirectWatch(),
  ]);
}
