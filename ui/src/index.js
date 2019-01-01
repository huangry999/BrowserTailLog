import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Root from './Root';
import * as serviceWorker from './serviceWorker';
import setupSocket from './socket/socket'

import { createStore, applyMiddleware } from 'redux'
import { createLogger } from 'redux-logger'
import rootReducer from './reducer/reducers'
import createSagaMiddleware from 'redux-saga'
import handleNewMessage from './saga/sagas'


const loggerMiddleware = createLogger()
const sagaMiddleware = createSagaMiddleware()

const store = createStore(rootReducer, applyMiddleware(
  sagaMiddleware,
  loggerMiddleware))

const socket = setupSocket(store.dispatch)
sagaMiddleware.run(handleNewMessage, { socket, dispatch: store.dispatch })

socket.onopen = () => {
  ReactDOM.render(<Root store={store} />, document.getElementById('root'));
}

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
