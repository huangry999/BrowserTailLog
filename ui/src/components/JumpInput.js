import React from 'react'
import { Row, Col, Icon, Input, Divider } from 'antd'

export default class JumpInmput extends React.Component {
  state = {
    showTakeInput: false,
  }
  from = undefined;
  take = this.props.take;

  onSubmit = () => {
    this.props.onSubmit(this.from, this.take);
    this.setState({ showTakeInput: false });
  }

  reverseShowTake = () => {
    this.setState({ showTakeInput: !this.state.showTakeInput })
  }

  render() {
    return (
      <Row>
        <Col>
          <Input
            style={{ width: 230 }}
            placeholder="jump to line"
            onChange={(val) => this.from = val.target.value}
            addonAfter={
              <div>
                <Icon type={this.state.showTakeInput ? "up" : "down"} onClick={this.reverseShowTake} />
                <Divider type="vertical" />
                <Icon type="arrow-right" onClick={this.onSubmit} />
              </div>
            }
            onPressEnter={this.onSubmit} />
        </Col>
        {
          this.state.showTakeInput ?
            <Col style={{ marginTop: '1rem' }}>
              <Input
                defaultValue={this.take}
                style={{ width: 230 }}
                onChange={(val) => this.take = val.target.value}
                addonBefore={"Take Count"}
                onPressEnter={this.onSubmit} />
            </Col>
            : undefined
        }

      </Row>
    );
  }
}