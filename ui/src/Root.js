import React, { Component } from 'react'
import { Provider } from 'react-redux'
import { HashRouter as Router, Route, Switch } from 'react-router-dom'
import Dashboarder from './Dashboarder'
import LogView from './containers/LogView'

export default class Root extends Component {
  render() {
    return (
      <Provider store={this.props.store}>
        <Router>
          <Switch>
            <Route path="/log" component={LogView} />
            <Route path="/" component={Dashboarder} />
          </Switch>
        </Router>
      </Provider>
    );
  }
}