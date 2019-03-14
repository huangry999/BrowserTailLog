import { doLogin, gotoHost } from '../action';
import LoginForm from '../components/LoginForm';
import { connect } from 'react-redux';

const mapStateToProps = ({ tipInfo, system, configs }) => {
  return { tip: tipInfo.loginTip, system, configs }
}

const mapDispatchToProps = (dispatch) => {
  return { dispatch }
}

const mergeProps = (stateProps, dispatchProps) => {
  const { dispatch } = dispatchProps;
  return {
    tip: stateProps.tip,
    onSubmit: (form) => {
      dispatch(doLogin(form.password));
    },
    componentDidMount: () => {
      if (!stateProps.configs.needAuth) {
        dispatch(gotoHost());
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