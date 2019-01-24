import { doLogin } from '../action';
import LoginForm from '../components/LoginForm';
import { connect } from 'react-redux';

const mapStateToProps = ({ tipInfo, system }) => {
  return { tip: tipInfo.loginTip, system }
}

const mapDispatchToProps = (dispatch) => {
  return {
    doLogin: (id, password) => {
      dispatch(doLogin(id, password));
    }
  }
}

const mergeProps = (stateProps, dispatchProps) => {
  return {
    tip: stateProps.tip,
    onSubmit: (form) => {
      const id = stateProps.system.sessionId;
      const crypto = require('crypto');
      const hash = crypto.createHash('sha256');
      hash.update(id + "34)8e$" + form.password);
      const hashPw = hash.digest('hex');
      dispatchProps.doLogin(id, hashPw);
    }
  }
}

const LoginPage = connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps
)(LoginForm)

export default LoginPage