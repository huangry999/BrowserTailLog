import React from 'react';
import { Table, Icon, Breadcrumb } from 'antd';
import { valueOf } from '../constant/FileType';
import moment from 'moment';
import FileSize from './FileSize';

const columns = [{
  render: record => <Icon type={valueOf(record.type).iconType} />,
  width: 50,
  key: 'icon',
}, {
  title: 'FileName',
  dataIndex: 'name',
  key: 'name',
}, {
  title: 'Size',
  render: record => <FileSize size={record.size} />,
  key: 'size',
}, {
  title: 'ModifyTime',
  render: record => record.modifyUtcTime ? moment(record.modifyUtcTime).format('YYYY-MM-DD HH:mm:ss') : undefined,
  key: 'modifyTime',
}];
export default class FileTable extends React.Component {

  componentDidMount() {
    this.props.init();
  }

  onClick = (record) => {
    if (this.props.onClick) {
      this.props.onClick(record);
    }
  }

  render() {
    const dirs = this.props.dir.current ? this.props.dir.current.split(/\\|\//).map(d =>
      <Breadcrumb.Item key={d}>
        <span>{d}</span>
      </Breadcrumb.Item>
    ) : null;
    return (
      <div>
        <Breadcrumb>
          <Breadcrumb.Item onClick={this.props.gotoHost} href="javascript:;" key='host'>
            <Icon type="home" />
            <span>Hosts List</span>
          </Breadcrumb.Item>
          <Breadcrumb.Item key={'selectedhost'}>
            {this.props.hostName}
          </Breadcrumb.Item>
          {dirs}
        </Breadcrumb>
        <Table
          columns={columns}
          dataSource={this.props.data}
          onRow={(record) => {
            return {
              onClick: () => this.onClick(record)
            };
          }}
        />
      </div>
    );
  }
}