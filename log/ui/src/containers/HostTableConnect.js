import { fetchHost, intoHost } from '../action'
import HostTable from '../components/HostTable'
import { connect } from 'react-redux'

const mapStateToProps = ({ host }) => {
  const hosts = Object.assign([], host.hosts);
  hosts.forEach(h => {
    h.key = h.name;
  });
  return { hosts };
}

const mapDispatchToProps = (dispatch) => {
  return {
    init: () => {
      dispatch(fetchHost());
    },
    onClick: (record) => {
      dispatch(intoHost(record));
    }
  }
}

const FileTableConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
)(HostTable)

export default FileTableConnect