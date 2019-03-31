import React from 'react'
import { Card, message } from 'antd'
import './LogComponent.css'
import LogHead from './LogHead'

export default class LogComponent extends React.Component {

  displayRequestLoadingMessage = false;

  componentDidMount() {
    this.props.loadLog();
    message.config({ top: 12 });
    const listener = window.addEventListener('scroll', () => {
      const wh = window.innerHeight;
      const dh = document.documentElement.scrollHeight;
      const scrollPercent = window.scrollY / (dh - wh);
      if (isNaN(scrollPercent)) {//refresh or no scroll
        return;
      }
      if (scrollPercent === 1) {
        this.clickToBottom();
      }
    });
  }

  componentDidUpdate() {
    this.scrollToBottom();
    if (this.props.log.logStatus.loading && !this.displayRequestLoadingMessage) {
      message.loading(this.props.tipInfo.loadingInfo, 0);
      this.displayRequestLoadingMessage = true
    } else if (!this.props.log.logStatus.loading) {
      message.destroy();
      this.displayRequestLoadingMessage = false;
    }
    this.props.cleanLogIfOverThreshold();
  }

  scrollToBottom() {
    if (this.props.log.logStatus.lockBottom) {
      this.bottom.scrollIntoView();
    }
  }

  clickToBottom = () => {
    if (!this.props.log.logStatus.lockBottom) {
      this.props.setLockBottom(true);
    }
  }

  wheelEventHandler = (e) => {
    const wh = window.innerHeight;
    const dh = document.documentElement.scrollHeight;
    const scrollPercent = window.scrollY / (dh - wh);
    if (scrollPercent === 0 && e.deltaY < 0) {
      this.props.fetchLogContentUp();
    }
    if (scrollPercent === 1 && e.deltaY > 0) {
      this.props.fetchLogContentDwon();
    }
    if (e.deltaY < 0 && this.props.log.logStatus.lockBottom) {
      this.props.setLockBottom(false);
    }
  }

  render() {
    let content = Object.values(this.props.log.data).map(d => <div className='log-row' key={d.lineNo}><span>{d.lineNo}</span><pre>{d.text}</pre></div>)
    return (
      <div onWheel={this.wheelEventHandler}>
        <Card
          title={<LogHead {...this.props} clickToBottom={this.clickToBottom} />}
          headStyle={{ position: 'fixed', backgroundColor: 'white', top: 0, width: '100%' }}
          bodyStyle={{ marginTop: 57, padding: 5 }}>
          {content}
        </Card>
        <div ref={bottom => { this.bottom = bottom; }}></div>
      </div>
    );
  }
}
