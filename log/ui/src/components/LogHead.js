import React from 'react'
import { Row, Col, Icon, Badge, Tooltip } from 'antd'
import './LogHead.css'
import ViewMode from '../constant/ViewMode'
import JumpInput from './JumpInput'

//const Search = Input.Search;
export default class LogHead extends React.Component {
  state = {
    showTools: false,
    spelLine: false,
  }

  gotoLineNo = undefined;
  goto = () => {
    const n = Number(this.gotoLineNo);
    if (!isNaN(n) && n > 0) {
      this.setState({ spelLine: true }, () => this.props.goto(parseInt(n, 10)));
    } else {
      this.reload();
    }
  }

  reload = () => {
    this.setState({ showTools: false, spelLine: false }, () => {
      this.gotoLineNo = undefined;
      this.props.loadLog();
    })
  }

  render() {
    const log = this.props.log;
    const status = log.logStatus;
    const showTools = this.state.showTools;
    return (
      <Row type='flex' justify="space-between" className='log-head'>
        <Col>
          <h3>{log.path}</h3>
        </Col>
        <Col>
          <Row type='flex' justify="space-between">
            {/*
            TODO 
            showTools ?
              <Col>
                <Search
                  placeholder="search by line"
                  onSearch={value => console.log(value)}
                  enterButton
                  addonAfter={<Icon type="down" />}
                />
              </Col>
              : undefined
            */}
            {showTools ?
              <Col>
                <JumpInput onSubmit={this.props.goto} take={this.props.configs.windowSize} />
              </Col>
              : undefined
            }
            <Col style={{ marginLeft: '1rem' }}>
              <Tooltip mouseEnterDelay={1} placement="bottom" title={this.state.showTools ? 'Close Tools' : 'Tools'}>
                <Icon type={showTools ? "minus-square" : "plus-square"} onClick={() => this.setState({ showTools: !showTools })} />
              </Tooltip>
            </Col>
            {
              status.mode === ViewMode.SCAN ?
                <Col style={{ marginLeft: '1rem' }}>
                  <Tooltip mouseEnterDelay={1} placement="bottom" title={status.receiveStatus.iconTip}>
                    <Badge status={status.receiveStatus.badge}>
                      <Icon type={status.receiveStatus.iconType} onClick={this.props.reverseReceiveStatus} />
                    </Badge>
                  </Tooltip>
                </Col>
                : undefined
            }

            <Col style={{ marginLeft: '1rem' }}>
              <Tooltip mouseEnterDelay={1} placement="bottom" title={status.mode.iconTip}>
                <Icon type={status.mode === ViewMode.SCAN ? "sync" : "scan"} onClick={this.reload} />
              </Tooltip>
            </Col>
            {
              status.mode === ViewMode.SCAN ?
                <Col style={{ marginLeft: '1rem' }}>
                  <Tooltip mouseEnterDelay={1} placement="bottom" title={'To End'}>
                    <Icon type="down-square" onClick={this.props.clickToBottom} theme={status.lockBottom ? 'twoTone' : 'outlined'} />
                  </Tooltip>
                </Col>
                : undefined
            }
          </Row>
        </Col>
      </Row>
    );
  }
}