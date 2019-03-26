import React from 'react'
import { Provider } from 'react-redux'
import { Route, Switch, Router } from 'react-router-dom'
import LogView from './containers/LogView'
import LoginPage from './containers/LoginPage'
import HostTableConnect from './containers/HostTableConnect'
import { createHashHistory } from 'history'
import { PersistGate } from 'redux-persist/integration/react'
import { persistor } from './config/configureStore'
import FileTableConnect from './containers/FileTableConnect';

export const history = createHashHistory();

const Root = ({ store }) => {
  return (
    <Provider store={store}>
      <PersistGate loading={null} persistor={persistor}>
        <Router history={history}>
          <Switch>
            <Route path="/:host/log/:key" component={LogView} />
            <Route path="/:host/log" component={FileTableConnect} />
            <Route path="/host" component={HostTableConnect} />
            <Route path="/" component={LoginPage} />
          </Switch>
        </Router>
      </PersistGate>
    </Provider >
  );
}
export default Root;