import React from 'react'
import { Card } from 'antd'
import PropTypes from 'prop-types'
import './TextDisplay.css'
import LogHead from './LogHead'

const TextDesplay = ({ log, reverseReceiveStatus }) => {
  let content = undefined;
  if (log.data) {
    content = Object.values(log.data).map(d => <p className='log-row' key={d.lineNo}><span>{d.lineNo}</span> {d.text}</p>)
  }
  return (
    <Card
      title={<LogHead log={log} reverseReceiveStatus={reverseReceiveStatus} />}
      headStyle={{ position: 'fixed', backgroundColor: 'white', top: 0, width: '100%' }}
      bodyStyle={{ marginTop: 57, padding: 5 }}>
      {content}
    </Card>
  );
}

TextDesplay.propTypes = {
  log: PropTypes.shape({
    path: PropTypes.string,
    data: PropTypes.arrayOf(PropTypes.shape({
      lineNo: PropTypes.number,
      text: PropTypes.string,
    })),
  })
}
export default TextDesplay;
