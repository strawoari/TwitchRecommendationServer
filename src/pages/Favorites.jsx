import { useState } from 'react'
import { Button, Drawer, Empty, Tag, Typography } from 'antd';
import { BookOutlined, StarFilled, UserOutlined, DownloadOutlined } from '@ant-design/icons';
import { getBookUrl } from '../utils';

const { Text } = Typography;

function FavoriteBookCard({ book }) {
  if (!book) return null

  const authors = book.authors?.join(', ') || 'Unknown Author';
  const subjects = book.subjects?.slice(0, 2) || [];

  return (
    <a
      href={getBookUrl(book)}
      target="_blank"
      rel="noopener noreferrer"
      style={{ textDecoration: 'none' }}
    >
      <div style={{
        display: 'flex',
        alignItems: 'center',
        gap: 12,
        padding: '10px 12px',
        borderRadius: 6,
        background: '#1a1a1a',
        border: '1px solid #222',
        marginBottom: 8,
        cursor: 'pointer',
        transition: 'border-color 0.2s',
      }}
        onMouseEnter={e => e.currentTarget.style.borderColor = '#e63232'}
        onMouseLeave={e => e.currentTarget.style.borderColor = '#222'}
      >
        {/* Cover Image */}
        {book.coverImage ? (
          <img
            src={book.coverImage}
            alt={book.title}
            style={{ width: 50, height: 70, objectFit: 'cover', borderRadius: 4, flexShrink: 0 }}
            onError={(e) => {
              e.target.style.display = 'none';
            }}
          />
        ) : (
          <div style={{
            width: 50, height: 70, borderRadius: 4, background: '#2a2a2a',
            display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
          }}>
            <BookOutlined style={{ color: '#555', fontSize: 20 }} />
          </div>
        )}

        {/* Info */}
        <div style={{ flex: 1, overflow: 'hidden' }}>
          <div style={{
            color: '#e0e0e0',
            fontSize: 13,
            fontWeight: 500,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            fontFamily: "'DM Sans', sans-serif",
          }}>
            {book.title || 'Untitled'}
          </div>
          <div style={{
            color: '#666',
            fontSize: 11,
            marginTop: 2,
            fontFamily: "'DM Mono', monospace",
            letterSpacing: '0.04em',
            display: 'flex',
            alignItems: 'center',
            gap: 4,
          }}>
            <UserOutlined style={{ fontSize: 10 }} />
            <span style={{
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
            }}>
              {authors}
            </span>
          </div>
          {subjects.length > 0 && (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4, marginTop: 6 }}>
              {subjects.map((subject, i) => (
                <Tag
                  key={i}
                  style={{
                    background: '#2a2a2a',
                    border: '1px solid #333',
                    color: '#888',
                    fontSize: 10,
                    borderRadius: 3,
                    margin: 0,
                  }}
                >
                  {subject.length > 15 ? subject.slice(0, 15) + '...' : subject}
                </Tag>
              ))}
            </div>
          )}
        </div>
      </div>
    </a>
  )
}

function FavoriteBookList({ books = [] }) {
  if (!books.length) {
    return (
      <Empty
        description={<span style={{ color: '#555', fontFamily: "'DM Mono', monospace", fontSize: 12 }}>No favorites saved yet</span>}
        style={{ marginTop: 48 }}
        image={<BookOutlined style={{ fontSize: 48, color: '#333' }} />}
      />
    )
  }
  return (
    <div style={{ padding: '4px 0' }}>
      {books.map((book, i) => <FavoriteBookCard key={book.gutenbergId || i} book={book} />)}
    </div>
  )
}

function Favorites({ favoriteItems }) {
  const [open, setOpen] = useState(false)
  const books = Array.isArray(favoriteItems) ? favoriteItems : [];

  return (
    <>
      <Button
        onClick={() => setOpen(true)}
        icon={<StarFilled style={{ color: '#e63232' }} />}
        style={{
          background: 'transparent',
          border: '1px solid #333',
          color: '#ccc',
          borderRadius: 4,
          height: 36,
          padding: '0 16px',
          fontFamily: "'DM Mono', monospace",
          fontSize: 12,
          letterSpacing: '0.08em',
          display: 'flex',
          alignItems: 'center',
          gap: 6,
        }}
        onMouseEnter={e => e.currentTarget.style.borderColor = '#e63232'}
        onMouseLeave={e => e.currentTarget.style.borderColor = '#333'}
      >
        FAVORITES {books.length > 0 && (
          <span style={{
            background: '#e63232',
            color: '#fff',
            borderRadius: 10,
            padding: '0 6px',
            fontSize: 10,
            fontWeight: 700,
            marginLeft: 2,
          }}>
            {books.length}
          </span>
        )}
      </Button>

      <Drawer
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <StarFilled style={{ color: '#e63232', fontSize: 16 }} />
            <span style={{
              fontFamily: "'Bebas Neue', 'Impact', sans-serif",
              fontSize: 20,
              letterSpacing: '0.1em',
              color: '#fff',
            }}>
              MY FAVORITES
            </span>
          </div>
        }
        placement="right"
        width={420}
        open={open}
        onClose={() => setOpen(false)}
        styles={{
          body: { background: '#111', padding: '16px 20px' },
          header: { background: '#0f0f0f', borderBottom: '1px solid #1f1f1f' },
          mask: { backdropFilter: 'blur(4px)', background: 'rgba(0,0,0,0.6)' },
        }}
      >
        <FavoriteBookList books={books} />
      </Drawer>
    </>
  )
}

export default Favorites
