import { doLogin, gotoHost, gotoLogin } from '../action';
import LoginForm from '../components/LoginForm';
import { connect } from 'react-redux';

const mapStateToProps = ({ tipInfo, system, configs }) => {
  return { tip: tipInfo.loginTip, system, needAuth: configs.needAuth }
}

const mapDispatchToProps = (dispatch) => {
  return { dispatch }
}

const mergeProps = (stateProps, dispatchProps) => {
  const { dispatch } = dispatchProps;
  return {
    tip: stateProps.tip,
    needAuth: stateProps.needAuth,
    onSubmit: (form) => {
      dispatch(doLogin(form.password));
    },
    autoLoginIfEnableAnonymous: () => {
      if (!stateProps.needAuth) {
        if (!stateProps.system.token) {
          dispatch(gotoLogin(), gotoHost);
        } else {
          dispatch(gotoHost());
        }
      }
    }
  }
}

const LoginPage = connect(
  mapStateToProps,
  mapDispatchToProps,
  mergeProps
)(LoginForm)

export default LoginPage