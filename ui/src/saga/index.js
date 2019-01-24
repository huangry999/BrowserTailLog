import { takeEvery, takeLatest } from 'redux-saga/effects'
import * as types from '../constant/ActionTypes'
import { encode } from '../protocol/ProtocolUtil'
import Request from '../protocol/Request'
import { getLogBetween } from '../action'
import { renderRoot } from '../index'

const handleNewMessage = function* handleNewMessage(params) {
  const { socket, store } = params;
  const { dispatch } = store;
  yield takeLatest(types.INIT, () => {
    const { system } = store.getState();
    const f = encode(Request.INIT, { id: system.sessionId, token: system.token });
    socket.send(f);
  })
  yield takeLatest(types.RESP_INIT, () => {
    renderRoot();
  })
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
  yield takeEvery(types.FIND_BY_LINE, (action) => {
    dispatch(getLogBetween(action.path, action.lineNo - 1, action.take));
  })
  yield takeEvery(types.LOGIN, (action) => {
    const f = encode(Request.LOGIN, { id: action.id, password: action.password });
    socket.send(f);
  })
}

export default handleNewMessage
