import { useState } from "react"
import { searchBooks } from '../utils'
import { message, Button, Modal, Form, Input } from 'antd'
import { SearchOutlined } from '@ant-design/icons';

function CustomSearch({ onSuccess, headerMode = false }) {
  const [displayModal, setDisplayModal] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleSearch = (query) => {
    if (!query || !query.trim()) return;
    setLoading(true);
    searchBooks(query.trim())
      .then((result) => {
        setLoading(false);
        setDisplayModal(false);
        onSuccess(result);
      })
      .catch((err) => {
        setLoading(false);
        message.error(err.message);
      });
  };

  const onSubmit = (data) => {
    handleSearch(data.book_query);
  };

  // Header mode: inline search input
  if (headerMode) {
    return (
      <>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, width: '100%', maxWidth: 480 }}>
          <Input
            placeholder="Search for a book..."
            prefix={<SearchOutlined style={{ color: '#555' }} />}
            onPressEnter={(e) => handleSearch(e.target.value)}
            style={{
              background: '#1a1a1a',
              border: '1px solid #2a2a2a',
              borderRadius: 4,
              color: '#e0e0e0',
              height: 38,
              flex: 1,
            }}
          />
          <Button
            loading={loading}
            icon={<SearchOutlined />}
            onClick={() => setDisplayModal(true)}
            style={{
              background: 'transparent',
              border: '1px solid #333',
              color: '#aaa',
              borderRadius: 4,
              height: 38,
              fontFamily: "'DM Mono', monospace",
              fontSize: 11,
              letterSpacing: '0.08em',
            }}
          >
            ADV
          </Button>
        </div>

        {/* Advanced search modal */}
        <Modal
          title={
            <span style={{
              fontFamily: "'Bebas Neue', 'Impact', sans-serif",
              fontSize: 20,
              letterSpacing: '0.1em',
              color: '#fff',
            }}>
              <span style={{ color: '#e63232' }}>—</span> SEARCH BOOKS
            </span>
          }
          open={displayModal}
          onCancel={() => setDisplayModal(false)}
          footer={null}
          destroyOnClose
          styles={{
            content: { background: '#111111', border: '1px solid #222', borderRadius: 6 },
            header: { background: '#111111', borderBottom: '1px solid #1f1f1f' },
            mask: { backdropFilter: 'blur(4px)', background: 'rgba(0,0,0,0.75)' },
          }}
        >
          <Form name="book_search_modal" onFinish={onSubmit} style={{ marginTop: 8 }}>
            <Form.Item name="book_query" rules={[{ required: true, message: 'Please enter a book title or author' }]}>
              <Input
                placeholder="Book title or author name"
                style={{
                  background: '#0a0a0a',
                  border: '1px solid #2a2a2a',
                  borderRadius: 4,
                  color: '#e0e0e0',
                  height: 42,
                }}
              />
            </Form.Item>
            <Form.Item style={{ marginBottom: 0, marginTop: 24 }}>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
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
                SEARCH
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </>
    )
  }

  // Default mode
  return (
    <>
      <Button
        onClick={() => setDisplayModal(true)}
        icon={<SearchOutlined />}
        style={{
          marginLeft: '16px',
          marginTop: '20px',
          background: 'transparent',
          border: '1px solid #333',
          color: '#ccc',
          borderRadius: 4,
          height: 36,
          fontFamily: "'DM Mono', monospace",
          fontSize: 12,
          letterSpacing: '0.08em',
        }}
      >
        Search Books
      </Button>
      <Modal
        title="Search Books"
        open={displayModal}
        onCancel={() => setDisplayModal(false)}
        footer={null}
        destroyOnClose
      >
        <Form name="book_search" onFinish={onSubmit}>
          <Form.Item name="book_query" rules={[{ required: true, message: 'Please enter a book title or author' }]}>
            <Input placeholder="Book title or author name" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>Search</Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}

export default CustomSearch
