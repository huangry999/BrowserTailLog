import { updateFileList, getInit, getNewLogContent, logContentBetween } from '../action/actions'
import { decode } from '../protocol/ProtocolUtil'
import Respond from '../protocol/Respond'

const setupSocket = (dispatch) => {
  const socket = new WebSocket('ws://192.168.1.101:8081/log')
  socket.onmessage = (event) => {
    decode(event.data)
      .then(({ respond, data, mode }) => {
        switch (respond) {
          case Respond.INIT:
            dispatch(getInit(data.rootDir));
            break;
          case Respond.LIST_FILE:
            dispatch(updateFileList(data.files, data.dir, data.rollback));
            break;
          case Respond.NEW_LOG_CONTENT:
            dispatch(getNewLogContent(data.path, data.data, mode));
            break;
          case Respond.LOG_CONTENT_BETWEEN:
            dispatch(logContentBetween(data.path, data.data));
            break;
          default:
            break;
        }
      });
  }
  return socket;
}

export default setupSocket
