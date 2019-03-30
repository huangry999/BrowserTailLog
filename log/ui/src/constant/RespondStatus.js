import PropTypes from 'prop-types';

const RespondStatus = {
  SUCCESS: {
    code: 0,
  },
  UNAUTHORIZED: {
    code: 401,
  },
  DECODE_ERROR: {
    code: 400,
  },
  INTERNAL_SERVER_ERROR: {
    code: 500,
  }
}

export const valueOf = code => Object.values(RespondStatus).find(t => t.code === code) || RespondStatus.SUCCESS;

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}

export default RespondStatus;