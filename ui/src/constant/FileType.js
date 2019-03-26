import PropTypes from 'prop-types';

const FileType = {
  LOG: {
    code: 0,
    iconType: 'file',
  },
  DIRECTORY: {
    code: 1,
    iconType: 'folder',
  },
  PARENT: {
    code: 2,
    iconType: 'rollback',
  }
}

export const valueOf = code => Object.values(FileType).find(t => t.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}

export default FileType;