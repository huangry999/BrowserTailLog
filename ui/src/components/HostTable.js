import React from 'react';
import { Table } from 'antd';

const columns = [{
  title: 'HostName',
  dataIndex: 'name',
  key: 'name',
}, {
  title: 'InternalAddress',
  dataIndex: 'ip',
  key: 'ip',
}];
export default class HostTable extends React.Component {

  componentDidMount() {
    this.props.init();
  }

  onClick = (record) => {
    if (this.props.onClick) {
      this.props.onClick(record);
    }
  }

  render() {
    return (
      <Table
        columns={columns}
        dataSource={this.props.hosts}
        onRow={(record) => {
          return {
            onClick: () => this.onClick(record)
          };
        }}
      />
    );
  }
}