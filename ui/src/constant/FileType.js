import PropTypes from 'prop-types';

const FileType = {
  DIRECTORY: {
    code: 1,
    iconType: 'folder',
  },
  LOG: {
    code: 2,
    iconType: 'file',
  },
  PARENT: {
    code: 3,
    iconType: 'rollback',
  }
}

export const valueOf = code => Object.values(FileType).find(t => t.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}

export default FileType;