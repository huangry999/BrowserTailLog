import TextDesplay from '../components/TextDisplay'
import { connect } from 'react-redux'
import { openLog, getLogBetween, reverseReceiveStatus } from '../action/actions'

let lastRowNo = undefined;
const mapStateToProps = (state = { log: {} }) => {
  if (state.log && state.log.data && state.log.data.length > 0) {
    lastRowNo = state.log.data.slice(-1).pop().lineNo;
  }
  return { log: state.log };
}

const mapDispatchToProps = (dispatch, ownProps) => {
  const query = new URLSearchParams(ownProps.location.search);
  const path = query.get('path');
  dispatch(openLog(path));
  const listener = window.addEventListener('scroll', (e) => {
    if (!lastRowNo || lastRowNo === 1) {
      return;
    }
    const wh = window.innerHeight;
    const dh = document.documentElement.scrollHeight;
    const scrollPercent = window.scrollY / (dh - wh);
    if (scrollPercent > 0.75) {
      dispatch(getLogBetween(path, lastRowNo - 1));
    }
  });
  return {
    reverseReceiveStatus: () => dispatch(reverseReceiveStatus()),
  }
}

const LogView = connect(
  mapStateToProps,
  mapDispatchToProps
)(TextDesplay)

export default LogView