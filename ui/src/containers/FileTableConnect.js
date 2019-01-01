import { intoDir, init } from '../action/actions';
import FileType, { valueOf } from '../constant/FileType';
import FileTable from '../components/FileTable';
import { connect } from 'react-redux';

const mapStateToProps = (state = { files: [], dir: {} }) => {
  const data = Object.assign([], state.files);
  if (state.dir.current) {
    const parent = {
      name: '../',
      type: FileType.PARENT.code,
      path: state.dir.rollback,
    };
    data.unshift(parent);
  }
  return { data };
}

const mapDispatchToProps = (dispatch, ownProps) => {
  dispatch(init());
  return {
    onClick: (record) => {
      switch (valueOf(record.type)) {
        case FileType.LOG:
          ownProps.history.push('/log?path=' + record.path);
          //window.open('/#/log?path=' + record.path);
          break;
        case FileType.DIRECTORY:
        case FileType.PARENT:
          dispatch(intoDir(record.path));
          break;
        default:
          dispatch(intoDir());
      }
    }
  }
}

const FileTableConnect = connect(
  mapStateToProps,
  mapDispatchToProps
)(FileTable)

export default FileTableConnect