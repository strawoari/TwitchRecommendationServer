import { Button, Card, List, message, Tabs, Tooltip, Empty } from 'antd';
import { StarOutlined, StarFilled, PlayCircleOutlined } from '@ant-design/icons';
import { addFavoriteItem, deleteFavoriteItem, getItemUrl } from '../utils';

 
// ✅ UI FIX: dark card styles matching red-black theme
const cardStyle = {
  background: '#1a1a1a',
  border: '1px solid #222',
  borderRadius: 6,
  overflow: 'hidden',
}
 
const cardHeadStyle = {
  background: '#161616',
  borderBottom: '1px solid #222',
  color: '#e0e0e0',
  fontSize: 12,
  padding: '0 10px',
  minHeight: 44,
}
 
const processUrl = (url) => url
  .replace('%{height}', '252')
  .replace('%{width}', '480')
  .replace('{height}', '252')
  .replace('{width}', '480');
 
const renderCardTitle = (item, loggedIn, favs = [], favOnChange) => {
  const title = `${item.broadcaster_name} - ${item.title}`;
  const isFav = favs.find((fav) => fav.twitch_id === item.twitch_id);
 
  const favOnClick = () => {
    const action = isFav ? deleteFavoriteItem : addFavoriteItem;
    action(item)
      .then(() => favOnChange())
      .catch(err => message.error(err.message));
  }
 
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      {loggedIn && (
        // ✅ UI FIX: star button styled to match theme
        <Tooltip title={isFav ? "Remove from favorites" : "Add to favorites"}>
          <Button
            shape="circle"
            size="small"
            icon={isFav ? <StarFilled style={{ color: '#e63232' }} /> : <StarOutlined style={{ color: '#555' }} />}
            onClick={favOnClick}
            style={{
              background: 'transparent',
              border: `1px solid ${isFav ? '#e63232' : '#333'}`,
              flexShrink: 0,
            }}
          />
        </Tooltip>
      )}
      <div style={{
        overflow: 'hidden',
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
        color: '#ccc',
        fontSize: 12,
        fontFamily: "'DM Sans', sans-serif",
      }}>
        <Tooltip title={title}>
          <span>{title}</span>
        </Tooltip>
      </div>
    </div>
  )
}
 
const renderCardGrid = (data, loggedIn, favs, favOnChange) => {
  // ✅ UI FIX: proper empty state instead of blank area
  if (!data || data.length === 0) {
    return (
      <Empty
        image={<PlayCircleOutlined style={{ fontSize: 48, color: '#333' }} />}
        description={
          <span style={{
            color: '#555',
            fontFamily: "'DM Mono', monospace",
            fontSize: 12,
            letterSpacing: '0.05em',
          }}>
            No content to display — select a game or search above
          </span>
        }
        style={{ marginTop: 64 }}
      />
    )
  }
 
  return (
    <List
      grid={{ xs: 1, sm: 2, md: 3, lg: 4, xl: 4, xxl: 6 }}
      dataSource={data}
      renderItem={item => (
        <List.Item style={{ marginRight: 16, marginBottom: 16 }}>
          <Card
            title={renderCardTitle(item, loggedIn, favs, favOnChange)}
            style={cardStyle}
            headStyle={cardHeadStyle}
            bodyStyle={{ padding: 0 }}
            // ✅ UI FIX: red border on hover
            onMouseEnter={e => e.currentTarget.style.borderColor = '#e63232'}
            onMouseLeave={e => e.currentTarget.style.borderColor = '#222'}
          >
            <a
            href={getItemUrl(item)}   // ← was: href={item.url}
            target="_blank"
            rel="noopener noreferrer"
            style={{ display: 'block', width: '100%' }}
            >
                <img
                    alt={item.title || 'thumbnail'}
                    src={processUrl(item.thumbnail_url)}
                    style={{ width: '100%', display: 'block' }}
                />
            </a>
          </Card>
        </List.Item>
      )}
    />
  )
}
 
const Home = ({ resources, loggedIn, favoriteItems, favoriteOnChange }) => {
  const { videos = [], streams = []} = resources;
  const { videos: favVideos = [], streams: favStreams = []} = favoriteItems || {};
 
  // ✅ FIX: replaced deprecated TabPane with items prop (Ant Design v5)
  // ✅ UI FIX: red active tab indicator via CSS override
  const tabItems = [
    {
      key: 'stream',
      label: 'Streams',
      children: renderCardGrid(streams, loggedIn, favStreams, favoriteOnChange),
    },
    {
      key: 'videos',
      label: 'Videos',
      children: renderCardGrid(videos, loggedIn, favVideos, favoriteOnChange),
    },
  ]
 
  return (
    <>
      {/* ✅ UI FIX: inline style override to turn tab indicator from blue → red */}
      <style>{`
        .home-tabs .ant-tabs-ink-bar { background: #e63232 !important; }
        .home-tabs .ant-tabs-tab-active .ant-tabs-tab-btn { color: #e63232 !important; }
        .home-tabs .ant-tabs-tab:hover .ant-tabs-tab-btn { color: #e63232 !important; }
        .home-tabs .ant-tabs-tab-btn { color: #888; }
        .home-tabs .ant-tabs-nav::before { border-color: #222 !important; }
      `}</style>
      <Tabs
        className="home-tabs"
        defaultActiveKey="stream"
        items={tabItems}
        destroyInactiveTabPane={false}
      />
    </>
  )
}
 
export default Home;
