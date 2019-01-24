import PropTypes from 'prop-types';

const Request = {
  INIT: {
    code: 1,
  },
  SUBSCRIBE: {
    code: 2,
  },
  CANCEL_SUBSCRIBE: {
    code: 3,
  },
  REQUEST_BETWEEN: {
    code: 4,
  },
  CHANGE_DIR: {
    code: 5,
  },
  LOGIN: {
    code: 6,
  },
}

export default Request;

export const valueOf = (code) => Object.values(Request).find(r => r.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}
