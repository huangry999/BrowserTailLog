import { intoDir, openLog } from '../action'
import FileType, { valueOf } from '../constant/FileType'
import FileTable from '../components/FileTable'
import { connect } from 'react-redux'

const mapStateToProps = ({ files, dir }) => {
  const data = Object.assign([], files);
  if (dir.current) {
    const parent = {
      name: '../',
      type: FileType.PARENT.code,
      path: dir.rollback,
      key: 'parentDir',
    };
    data.unshift(parent);
  }
  return { dir, data };
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
    }
  }
}

const mergeProps = (stateProps, dispatchProps) => {
  const { dispatch } = dispatchProps;
  return {
    data: stateProps.data,
    onClick: dispatchProps.onClick,
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