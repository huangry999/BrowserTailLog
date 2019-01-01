import { takeEvery, takeLatest } from 'redux-saga/effects'
import * as types from '../constant/ActionTypes'
import { encode } from '../protocol/ProtocolUtil'
import Request from '../protocol/Request'
import { intoDir } from '../action/actions'

const handleNewMessage = function* handleNewMessage(params) {
  const { socket, dispatch } = params;
  yield takeLatest(types.INIT, (action) => {
    const f = encode(Request.INIT);
    socket.send(f);
  })
  yield takeLatest(types.RESP_INIT, (action) => {
    dispatch(intoDir());
  })
  yield takeLatest(types.INTO_DIR, (action) => {
    const f = encode(Request.CHANGE_DIR, { path: action.dir });
    socket.send(f);
  })
  yield takeLatest(types.OPEN_LOG, (action) => {
    const f = encode(Request.SUBSCRIBE, { path: action.path });
    socket.send(f);
  })
  yield takeEvery(types.GET_LOG_BETWEEN, (action) => {
    const f = encode(Request.REQUEST_BETWEEN, { path: action.path, from: action.from, take: action.take });
    socket.send(f);
  })
}

export default handleNewMessage
