import PropTypes from 'prop-types';

const Respond = {
  INIT: {
    code: 1,
  },
  LIST_FILE: {
    code: 2,
  },
  NEW_LOG_CONTENT: {
    code: 3,
  },
  LOG_CONTENT_BETWEEN:{
    code: 4,
  },
  LOGIN:{
    code: 5,
  }
}

export default Respond;

export const valueOf = (code) => Object.values(Respond).find(r => r.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}
