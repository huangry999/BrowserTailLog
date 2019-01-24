import { updateFileList, getInit, getNewLogContent, logContentBetween, gotoLogin, loginSuccess } from '../action'
import { decode } from '../protocol/ProtocolUtil'
import Respond from '../protocol/Respond'
import RespondStatus, { valueOf } from '../constant/RespondStatus'

const setupSocket = (dispatch) => {
  const socket = new WebSocket('ws://192.168.1.101:8081/log');
  socket.onerror = (event) => {
    console.log(event);
  }

  socket.onmessage = (event) => {
    decode(event.data)
      .then(({ respond, data, mode }) => {
        const status = valueOf(data.respondStatus);
        if (status !== RespondStatus.SUCCESS) {
          switch (status) {
            case RespondStatus.UNAUTHORIZED:
              dispatch(gotoLogin(data.msg));
              return;
            default:
              return;
          }
        }
        switch (respond) {
          case Respond.INIT:
            dispatch(getInit(data));
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
          case Respond.LOGIN:
            dispatch(loginSuccess(data.token));
            break;
          default:
            break;
        }
      });
  }
  return socket;
}

export default setupSocket;
