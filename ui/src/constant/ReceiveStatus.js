import PropTypes from 'prop-types';

const ReceiveStatus = {
  RUNNING: {
    code: 1,
    iconType: 'pause',
    iconTip:'Pause Receive New',
    badge: 'success',
  },
  PAUSE: {
    code: 2,
    iconType: 'caret-right',
    iconTip:'Receive New',
    badge: 'warning',
  },
}

export const valueOf = code => Object.values(ReceiveStatus).find(t => t.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}

export default ReceiveStatus;