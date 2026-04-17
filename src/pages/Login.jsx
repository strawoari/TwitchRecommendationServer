import { Button, Form, Input, message, Modal } from 'antd'
import { useState } from 'react';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { login } from '../utils'
 
const modalStyles = {
  content: {
    background: '#111111',
    border: '1px solid #222',
    borderRadius: 6,
    padding: '32px 28px',
  },
  header: {
    background: '#111111',
    borderBottom: '1px solid #1f1f1f',
    paddingBottom: 16,
  },
  mask: {
    backdropFilter: 'blur(4px)',
    background: 'rgba(0,0,0,0.75)',
  },
}
 
const inputStyle = {
  background: '#0a0a0a',
  border: '1px solid #2a2a2a',
  borderRadius: 4,
  color: '#e0e0e0',
  height: 42,
}
 
function Login({ onSuccess }) {
  const [displayModal, setDisplayModal] = useState(false)
 
  const handleCancel = () => setDisplayModal(false)
  const signinOnClick = () => setDisplayModal(true)
 
  const onFinish = (data) => {
    login(data)
      .then(() => {
        setDisplayModal(false)
        message.success(`Welcome back`)
        onSuccess()
      }).catch((err) => {
        message.error(err.message)
      })
  }
 
  return (
    <>
      <Button
        onClick={signinOnClick}
        style={{
          background: 'transparent',
          border: '1px solid #333',
          color: '#ccc',
          borderRadius: 4,
          height: 36,
          padding: '0 20px',
          fontFamily: "'DM Mono', monospace",
          fontSize: 12,
          letterSpacing: '0.08em',
        }}
      >
        LOGIN
      </Button>
 
      <Modal
        title={
          <span style={{
            color: '#fff',
            fontFamily: "'Bebas Neue', 'Impact', sans-serif",
            fontSize: 22,
            letterSpacing: '0.1em',
          }}>
            <span style={{ color: '#e63232' }}>—</span> SIGN IN
          </span>
        }
        open={displayModal}
        onCancel={handleCancel}
        footer={null}
        destroyOnClose={true}
        styles={modalStyles}
      >
        <Form name="normal_login" onFinish={onFinish} preserve={false} style={{ marginTop: 8 }}>
          <Form.Item
            name="username"
            rules={[{ required: true, message: 'Username is required' }]}
          >
            <Input
              prefix={<UserOutlined style={{ color: '#555' }} />}
              placeholder="Username"
              style={inputStyle}
            />
          </Form.Item>
          <Form.Item
            name="password"
            rules={[{ required: true, message: 'Password is required' }]}
          >
            <Input.Password
              prefix={<LockOutlined style={{ color: '#555' }} />}
              placeholder="Password"
              style={inputStyle}
            />
          </Form.Item>
          <Form.Item style={{ marginBottom: 0, marginTop: 24 }}>
            <Button
              type="primary"
              htmlType="submit"
              style={{
                width: '100%',
                height: 42,
                background: '#e63232',
                border: 'none',
                borderRadius: 4,
                fontFamily: "'DM Mono', monospace",
                fontSize: 13,
                letterSpacing: '0.1em',
                fontWeight: 600,
              }}
            >
              LOGIN
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
 
export default Login
