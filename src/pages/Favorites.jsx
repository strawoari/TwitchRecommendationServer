import { useState } from 'react'
import { Button, Drawer, Tabs, Empty, Tag } from 'antd';
import { EyeOutlined, YoutubeOutlined, VideoCameraOutlined, StarFilled, PlayCircleOutlined } from '@ant-design/icons';
 
const TABS = [
  { key: 'streams', label: 'Streams', icon: <EyeOutlined /> },
  { key: 'videos', label: 'Videos', icon: <YoutubeOutlined /> },
]
 
function FavoriteCard({ item }) {
  if (!item) return null
  return (
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
      {/* Thumbnail */}
      {item.thumbnail_url ? (
        <img
          src={item.thumbnail_url.replace('{width}', '80').replace('{height}', '45')}
          alt={item.title}
          style={{ width: 80, height: 45, objectFit: 'cover', borderRadius: 4, flexShrink: 0 }}
        />
      ) : (
        <div style={{
          width: 80, height: 45, borderRadius: 4, background: '#2a2a2a',
          display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
        }}>
          <PlayCircleOutlined style={{ color: '#555', fontSize: 20 }} />
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
          {item.title || item.name || 'Untitled'}
        </div>
        <div style={{
          color: '#666',
          fontSize: 11,
          marginTop: 2,
          fontFamily: "'DM Mono', monospace",
          letterSpacing: '0.04em',
        }}>
          {item.user_name || item.broadcaster_name || ''}
          {item.game_name && (
            <Tag style={{
              marginLeft: 6,
              background: '#2a2a2a',
              border: '1px solid #333',
              color: '#888',
              fontSize: 10,
            }}>
              {item.game_name}
            </Tag>
          )}
        </div>
      </div>
    </div>
  )
}
 
function FavoriteList({ items = [] }) {
  if (!items.length) {
    return (
      <Empty
        description={<span style={{ color: '#555', fontFamily: "'DM Mono', monospace", fontSize: 12 }}>Nothing saved yet</span>}
        style={{ marginTop: 48 }}
        image={Empty.PRESENTED_IMAGE_SIMPLE}
      />
    )
  }
  return (
    <div style={{ padding: '4px 0' }}>
      {items.map((item, i) => <FavoriteCard key={i} item={item} />)}
    </div>
  )
}
 
function Favorites({ favoriteItems }) {
  const [open, setOpen] = useState(false)
  const { videos = [], streams = []} = favoriteItems || {}
 
  const total = videos.length + streams.length
 
  const tabItems = [
    {
      key: 'streams',
      label: <span><EyeOutlined /> Streams <span style={{ color: '#666', fontSize: 11 }}>({streams.length})</span></span>,
      children: <FavoriteList items={streams} />,
    },
    {
      key: 'videos',
      label: <span><YoutubeOutlined /> Videos <span style={{ color: '#666', fontSize: 11 }}>({videos.length})</span></span>,
      children: <FavoriteList items={videos} />,
    }
  ]
 
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
        FAVORITES {total > 0 && (
          <span style={{
            background: '#e63232',
            color: '#fff',
            borderRadius: 10,
            padding: '0 6px',
            fontSize: 10,
            fontWeight: 700,
            marginLeft: 2,
          }}>
            {total}
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
        width={480}
        open={open}
        onClose={() => setOpen(false)}
        styles={{
          body: { background: '#111', padding: '16px 20px' },
          header: { background: '#0f0f0f', borderBottom: '1px solid #1f1f1f' },
          mask: { backdropFilter: 'blur(4px)', background: 'rgba(0,0,0,0.6)' },
        }}
      >
        <Tabs
          defaultActiveKey="streams"
          items={tabItems}
          style={{ color: '#ccc' }}
        />
      </Drawer>
    </>
  )
}
 
export default Favorites