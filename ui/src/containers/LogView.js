import LogComponent from '../components/LogComponent'
import { connect } from 'react-redux'
import { loadLog, getLogBetween, setReceiveStatus, findByLine, setLockBottom, replaceLogContent } from '../action'
import ReceiveStatus from '../constant/ReceiveStatus'
import ViewMode from '../constant/ViewMode'

const mapStateToProps = ({ log, configs, tipInfo, openLogMap }) => {
  return { log, tipInfo, configs, openLogMap };
}

const mapDispatchToProps = (dispatch) => {
  return { dispatch }
}

const mergeProps = (stateProps, dispatchProps, ownProps) => {
  const path = stateProps.openLogMap[ownProps.match.params.key];
  const log = Object.assign({}, stateProps.log, { path: path });
  const { logStatus: status } = log;
  const { windowSize, shrinkThreshold } = stateProps.configs;
  const dataLen = log.data.length;
  const { dispatch } = dispatchProps;
  let topRowNo = 0;
  let bottomRowNo = 0;
  if (dataLen > 0) {
    topRowNo = log.data[0].lineNo;
    bottomRowNo = log.data.slice(-1)[0].lineNo;
  }
  return {
    ...stateProps,
    log,
    reverseReceiveStatus: () => {
      let newStatus = ReceiveStatus.RUNNING;
      if (status.receiveStatus === ReceiveStatus.RUNNING) {
        newStatus = ReceiveStatus.PAUSE;
      }
      dispatch(setReceiveStatus(newStatus));
    },
    setLockBottom: (isLock) => {
      if (status.mode === ViewMode.SCAN) {
        dispatch(setLockBottom(isLock));
      }
    },
    fetchLogContentUp: () => {
      if (!topRowNo || topRowNo === 1) {
        return;
      }
      const skip0 = topRowNo - windowSize - 1;
      const skip = skip0 > 0 ? skip0 : 0;
      const take = topRowNo - skip - 1;
      dispatch(getLogBetween(path, skip, take));
    },
    fetchLogContentDwon: () => {
      if (!bottomRowNo || status.receiveStatus === ReceiveStatus.RUNNING) {
        return;
      }
      dispatch(getLogBetween(path, bottomRowNo, windowSize));
    },
    cleanLogIfOverThreshold: () => {
      if (status.mode === ViewMode.SCAN &&
        status.lockBottom &&
        !status.loading &&
        !isNaN(shrinkThreshold) &&
        dataLen > shrinkThreshold) {
        const start = parseInt(dataLen - 0.8 * shrinkThreshold, 10);
        dispatch(replaceLogContent(log.data.slice(start)));
      }
    },
    loadLog: () => dispatch(loadLog(path)),
    goto: (lineNo, take) => {
      if (isNaN(lineNo) || isNaN(take)) {
        return;
      }
      dispatch(findByLine(path, parseInt(lineNo, 10), parseInt(take), 10));
    },
  }
}

const LogView = connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps
)(LogComponent)

export default LogView