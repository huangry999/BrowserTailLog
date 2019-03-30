import PropTypes from 'prop-types';

const Mode = {
  NONE:{
    code: 0,
  },
  DELETE:{
    code: 1,
  },
  CREATE:{
    code: 2,
  },
  MODIFY:{
    code: 3,
  },

}
export default Mode;

export const valueOf = (code) => Object.values(Mode).find(r => r.code === code);

valueOf.propTypes = {
  code: PropTypes.number.isRequired
}