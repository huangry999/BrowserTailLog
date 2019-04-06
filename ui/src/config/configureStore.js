import { applyMiddleware, createStore } from 'redux'
import createSagaMiddleware from 'redux-saga'
import { createLogger } from 'redux-logger'
import handleNewMessage from '../saga'
import setupSocket from '../socket'
import * as reduces from '../reducer'
import { persistStore, persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'
import { combineReducers } from 'redux'

const debug = localStorage.getItem("debug");

const loggerMiddleware = createLogger()
const sagaMiddleware = createSagaMiddleware()
const middleware = [debug && loggerMiddleware, sagaMiddleware].filter(Boolean);

const system = persistReducer({ key: 'system', storage }, reduces.system);
const openLogMap = persistReducer({ key: 'openLogMap', storage }, reduces.openLogMap);
const dir = persistReducer({ key: 'dir', storage }, reduces.dir);
const host = persistReducer({ key: 'host', storage }, reduces.host);

export const store = createStore(combineReducers({
  ...reduces,
  system,
  openLogMap,
  dir,
  host,
}), applyMiddleware(...middleware))
export const persistor = persistStore(store);
const socket = setupSocket(store.dispatch);
sagaMiddleware.run(handleNewMessage, { socket, store });