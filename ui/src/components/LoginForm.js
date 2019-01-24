import React from 'react'
import { Form, Icon, Input, Button } from 'antd'
import './LoginForm.css'

class NormalLoginForm extends React.Component {
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        this.props.onSubmit(values);
      }
    });
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <Form onSubmit={this.handleSubmit} className="login-form">
        <Form.Item
          validateStatus="warning"
          help={this.props.tip}
        >
          {getFieldDecorator('password', {
            rules: [
              { required: true, message: 'Please input your Password!' },

            ],
          })(
            <Input prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder="Password" />
          )}
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" className="login-form-button">
            Log in
          </Button>
        </Form.Item>
      </Form>
    );
  }
}
const LoginForm = Form.create({ name: 'normal_login' })(NormalLoginForm);
export default LoginForm;