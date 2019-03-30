import PropTypes from 'prop-types';

const ViewMode = {
  SCAN: {
    code: 1,
    iconTip: 'Reload',
  },
  FIND_LINE: {
    code: 2,
    iconTip: 'Scan Mode',
  },
  SEARCH: {
    code: 3,
  }
}

export const valueOf = code => Object.values(ViewMode).find(t => t.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}

export default ViewMode;