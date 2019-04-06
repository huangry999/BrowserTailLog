import { updateFileList, getNewLogContent, logContentBetween, gotoLogin, init, wsError } from '../action'
import { decode } from '../protocol/ProtocolUtil'
import Respond from '../protocol/Respond'
import RespondStatus, { valueOf } from '../constant/RespondStatus'
import Config from '../config';

const setupSocket = (dispatch) => {
  const socket = new WebSocket(`ws://${Config.ip}:${Config.wsPort}/log`);
  socket.onerror = (event) => {
    dispatch(wsError(event));
    console.error(event);
  }
  socket.onopen = () => {
    console.info("websocket opened");
    dispatch(init());
  }
  socket.onclose = () => {
    dispatch(wsError('websocket close'));
    console.info("websocket close");
  }

  socket.onmessage = (event) => {
    decode(event.data)
      .then(({ respond, data, mode }) => {
        const status = valueOf(data.respondStatus);
        if (status !== RespondStatus.SUCCESS) {
          switch (status) {
            case RespondStatus.UNAUTHORIZED:
              console.warn("websocket respond unauthorized, goto golin");
              dispatch(gotoLogin(data.msg));
              return;
            default:
              dispatch(wsError(data.msg));
              console.error(data.msg);
              return;
          }
        }
        switch (respond) {
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

export default setupSocket;
