import { updateFileList, getNewLogContent, logContentBetween, gotoLogin, init } from '../action'
import { decode } from '../protocol/ProtocolUtil'
import Respond from '../protocol/Respond'
import RespondStatus, { valueOf } from '../constant/RespondStatus'

const setupSocket = (dispatch) => {
  const socket = new WebSocket('ws://192.168.1.101:8081/log');
  socket.onerror = (event) => {
    console.error(event);
  }
  socket.onopen = () => {
    console.info("websocket opened");
    dispatch(init());
  }
  socket.onclose = () => {
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
