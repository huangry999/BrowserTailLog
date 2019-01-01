import React from 'react'
import { Row, Col, Icon, Badge } from 'antd'
import PropTypes from 'prop-types'
import './LogHead.css'

const LogHead = ({ log, reverseReceiveStatus }) => {
  return (
    <Row type='flex' justify="space-between" className='log-head'>
      <Col>
        <h3>{log.path}</h3>
      </Col>
      <Col>
        <Row type='flex' justify="space-between">
          <Col><Badge status={log.receiveStatus.badge}>
            <Icon type={log.receiveStatus.iconType} onClick={reverseReceiveStatus} />
          </Badge></Col>
          <Col style={{ marginLeft: '1rem' }}>
            <Icon type="arrow-up" onClick={() => window.document.documentElement.scrollTo(0, 0)} />
          </Col>
        </Row>
      </Col>
    </Row>
  );
}

LogHead.propTypes = {
  log: PropTypes.shape({
    path: PropTypes.string,
    data: PropTypes.arrayOf(PropTypes.shape({
      lineNo: PropTypes.number,
      text: PropTypes.string,
    })),
  })
}
export default LogHead;