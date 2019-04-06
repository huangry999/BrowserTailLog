import { intoDir, openLog, gotoHost } from '../action'
import FileType, { valueOf } from '../constant/FileType'
import FileTable from '../components/FileTable'
import { connect } from 'react-redux'

const mapStateToProps = ({ files, dir, host }) => {
  const data = Object.assign([], files);
  const { rollback } = dir;
  const parent = {
    name: '../',
    type: FileType.PARENT.code,
    path: rollback.rollBackPath,
    key: 'parentDir',
  };
  if (rollback.inRootPath) {
    parent.key = 'toRoot';
  }
  if (!rollback.inHostPath) {
    data.unshift(parent);
  }
  return { dir, data, hostName: host.currentHost.name };
}

const mapDispatchToProps = (dispatch) => {
  return {
    dispatch,
    onClick: (record) => {
      switch (valueOf(record.type)) {
        case FileType.LOG:
          dispatch(openLog(record.key));
          break;
        case FileType.DIRECTORY:
        case FileType.PARENT:
          dispatch(intoDir(record.path));
          break;
        default:
          throw Error("Not support file tpye: " + record.type);
      }
    },
    gotoHost: () => {
      dispatch(gotoHost())
    },
    intoDir: (path) => dispatch(intoDir(path)),
  }
}

const mergeProps = (stateProps, dispatchProps) => {
  const { dispatch } = dispatchProps;
  return {
    ...stateProps,
    ...dispatchProps,
    init: () => {
      dispatch(intoDir(stateProps.dir.current));
    }
  }
}

const FileTableConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps,
)(FileTable)

export default FileTableConnect