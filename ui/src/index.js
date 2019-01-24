import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Root from './Root';
import * as serviceWorker from './serviceWorker'
import { init } from './action'
import { socket, store } from './config/configureStore'

socket.onopen = () => {
  store.dispatch(init());
}
let hasRender = false
export function renderRoot() {
  if (hasRender) {
    return;
  }
  ReactDOM.render(<Root store={store} />, document.getElementById('root'));
  hasRender = true;
}

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
