import React from 'react';
import PropTypes from 'prop-types';

/**
 * format file size
 * 
 * @param {number} size size of file, unit is byte 
 */
const FileSize = ({ size }) => {
  if (!size && size !== 0) {
    return '-';
  }
  const units = ['B', 'K', 'M', 'G', 'T', 'P'];
  let s = size;
  let i = 0;
  while ((s /= 1024) >= 1) {
    i++;
  }
  return <span>{`${(s * 1024).toFixed(0)} ${units[i]}`}</span>
}

FileSize.propTypes = {
  size: PropTypes.number
}
export default FileSize;