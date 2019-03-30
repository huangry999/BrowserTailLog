import { Menu, Icon, Row } from 'antd';
import React from 'react';
import FileTableConnect from './containers/FileTableConnect';

const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

export default class Dashboarder extends React.Component {
  state = {
    current: 'mail',
  }
  
  handleClick = (e) => {
    console.log('click ', e);
    this.setState({
      current: e.key,
    });
  }

  render() {
    return (
      <div>
        <Row>
          <Menu
            onClick={this.handleClick}
            selectedKeys={[this.state.current]}
            mode="horizontal">
            <Menu.Item key="mail">
              <Icon type="mail" />Navigation One
          </Menu.Item>
            <SubMenu title={<span className="submenu-title-wrapper"><Icon type="setting" />Navigation Three - Submenu</span>}>
              <MenuItemGroup title="Item 1">
                <Menu.Item key="setting:1">Option 1</Menu.Item>
                <Menu.Item key="setting:2">Option 2</Menu.Item>
              </MenuItemGroup>
              <MenuItemGroup title="Item 2">
                <Menu.Item key="setting:3">Option 3</Menu.Item>
                <Menu.Item key="setting:4">Option 4</Menu.Item>
              </MenuItemGroup>
            </SubMenu>
          </Menu>
        </Row>
        <Row>
          <FileTableConnect {...this.props} />
        </Row>
      </div>
    );
  }
}