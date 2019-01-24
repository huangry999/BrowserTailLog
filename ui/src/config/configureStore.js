import { applyMiddleware, createStore } from 'redux'
import createSagaMiddleware from 'redux-saga'
import { createLogger } from 'redux-logger'
import handleNewMessage from '../saga'
import setupSocket from '../socket'
import * as reduces from '../reducer'
import { persistStore, persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'
import { combineReducers } from 'redux'

const loggerMiddleware = createLogger()
const sagaMiddleware = createSagaMiddleware()

const system = persistReducer({ key: 'system', storage }, reduces.system);
const openLogMap = persistReducer({ key: 'openLogMap', storage }, reduces.openLogMap);
const dir = persistReducer({ key: 'dir', storage }, reduces.dir);

export const store = createStore(combineReducers({
  ...reduces,
  system,
  openLogMap,
  dir,
}), applyMiddleware(
  sagaMiddleware,
  loggerMiddleware))
export const persistor = persistStore(store);
export const socket = setupSocket(store.dispatch);
sagaMiddleware.run(handleNewMessage, { socket, store });