import { fetchHost, intoHost } from '../action'
import HostTable from '../components/HostTable'
import { connect } from 'react-redux'

const mapStateToProps = ({ host }) => {
  const hosts = Object.assign([], host.hosts);
  hosts.forEach(h => {
    h.key = h.ip;
  });
  return { hosts };
}

const mapDispatchToProps = (dispatch) => {
  return {
    init: () => {
      dispatch(fetchHost());
    },
    onClick: (record) => {
      dispatch(intoHost(record.ip));
    }
  }
}

const FileTableConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
)(HostTable)

export default FileTableConnect