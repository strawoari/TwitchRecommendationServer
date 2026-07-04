import { Button, Card, List, message, Tabs, Tooltip, Empty, Tag, Typography } from 'antd';
import { StarOutlined, StarFilled, BookOutlined, DownloadOutlined, UserOutlined, HeartOutlined } from '@ant-design/icons';
import { addFavoriteItem, deleteFavoriteItem, getBookUrl } from '../utils';

const { Text, Paragraph } = Typography;

// ─── Styles ────────────────────────────────────────────────────────

const cardStyle = {
  background: '#1a1a1a',
  border: '1px solid #222',
  borderRadius: 8,
  overflow: 'hidden',
  transition: 'border-color 0.2s, transform 0.2s',
  height: '100%',
}

const cardHeadStyle = {
  background: '#161616',
  borderBottom: '1px solid #222',
  color: '#e0e0e0',
  fontSize: 13,
  padding: '0 12px',
  minHeight: 48,
}

// ─── Book Card ─────────────────────────────────────────────────────

const BookCard = ({ book, loggedIn, favoriteBooks = [], favoriteOnChange }) => {
  const isFav = favoriteBooks.some((fav) => fav.gutenbergId === book.gutenbergId);

  const favOnClick = () => {
    const action = isFav ? deleteFavoriteItem : addFavoriteItem;
    action(book)
      .then(() => favoriteOnChange())
      .catch(err => message.error(err.message));
  }

  const authors = book.authors?.join(', ') || 'Unknown Author';
  const subjects = book.subjects?.slice(0, 3) || [];

  return (
    <Card
      style={cardStyle}
      styles={{ header: cardHeadStyle, body: { padding: 0 } }}
      title={
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          {loggedIn && (
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
            fontSize: 13,
            fontFamily: "'DM Sans', sans-serif",
          }}>
            <Tooltip title={book.title}>
              <span>{book.title}</span>
            </Tooltip>
          </div>
        </div>
      }
      onMouseEnter={e => {
        e.currentTarget.style.borderColor = '#e63232';
        e.currentTarget.style.transform = 'translateY(-2px)';
      }}
      onMouseLeave={e => {
        e.currentTarget.style.borderColor = '#222';
        e.currentTarget.style.transform = 'translateY(0)';
      }}
    >
      <a
        href={getBookUrl(book)}
        target="_blank"
        rel="noopener noreferrer"
        style={{ display: 'block', width: '100%' }}
      >
        {/* Cover Image */}
        <div style={{
          width: '100%',
          aspectRatio: '3/4',
          background: '#0f0f0f',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          overflow: 'hidden',
        }}>
          {book.coverImage ? (
            <img
              alt={book.title}
              src={book.coverImage}
              style={{
                width: '100%',
                height: '100%',
                objectFit: 'cover',
                display: 'block',
              }}
              onError={(e) => {
                e.target.style.display = 'none';
                e.target.nextSibling.style.display = 'flex';
              }}
            />
          ) : null}
          <div style={{
            display: book.coverImage ? 'none' : 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 8,
            color: '#333',
          }}>
            <BookOutlined style={{ fontSize: 48 }} />
            <span style={{ fontSize: 11, fontFamily: "'DM Mono', monospace" }}>No Cover</span>
          </div>
        </div>
      </a>

      {/* Book Info */}
      <div style={{ padding: '12px 14px' }}>
        {/* Author */}
        <div style={{
          color: '#888',
          fontSize: 11,
          fontFamily: "'DM Mono', monospace",
          marginBottom: 8,
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

        {/* Subjects */}
        {subjects.length > 0 && (
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4, marginBottom: 8 }}>
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
                {subject.length > 20 ? subject.slice(0, 20) + '...' : subject}
              </Tag>
            ))}
          </div>
        )}

        {/* Download Count */}
        {book.downloadCount && (
          <div style={{
            color: '#555',
            fontSize: 10,
            fontFamily: "'DM Mono', monospace",
            display: 'flex',
            alignItems: 'center',
            gap: 4,
          }}>
            <DownloadOutlined />
            <span>{book.downloadCount.toLocaleString()} downloads</span>
          </div>
        )}

        {/* Sneak Peek */}
        {book.sneakPeek && (
          <Tooltip title={book.sneakPeek}>
            <Paragraph
              style={{
                color: '#666',
                fontSize: 11,
                fontFamily: "'DM Sans', sans-serif",
                marginTop: 8,
                marginBottom: 0,
                lineHeight: 1.5,
              }}
              ellipsis={{ rows: 2 }}
            >
              {book.sneakPeek}
            </Paragraph>
          </Tooltip>
        )}
      </div>
    </Card>
  );
};

// ─── Book Grid ─────────────────────────────────────────────────────

const BookGrid = ({ books, loggedIn, favoriteBooks, favoriteOnChange }) => {
  if (!books || books.length === 0) {
    return (
      <Empty
        image={<BookOutlined style={{ fontSize: 48, color: '#333' }} />}
        description={
          <span style={{
            color: '#555',
            fontFamily: "'DM Mono', monospace",
            fontSize: 12,
            letterSpacing: '0.05em',
          }}>
            No books to display — try searching above
          </span>
        }
        style={{ marginTop: 64 }}
      />
    )
  }

  return (
    <List
      grid={{ xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }}
      dataSource={books}
      renderItem={book => (
        <List.Item style={{ marginRight: 16, marginBottom: 16 }}>
          <BookCard
            book={book}
            loggedIn={loggedIn}
            favoriteBooks={favoriteBooks}
            favoriteOnChange={favoriteOnChange}
          />
        </List.Item>
      )}
    />
  )
}

// ─── Home Component ────────────────────────────────────────────────

const Home = ({ searchResults, recommendations, loggedIn, favoriteBooks, favoriteOnChange }) => {
  // If search results are present, show them
  if (searchResults && searchResults.books && searchResults.books.length > 0) {
    return (
      <div>
        <div style={{
          color: '#fff',
          fontFamily: "'Bebas Neue', 'Impact', sans-serif",
          fontSize: 22,
          letterSpacing: '0.1em',
          marginBottom: 24,
          display: 'flex',
          alignItems: 'center',
          gap: 10,
        }}>
          <span style={{ color: '#e63232' }}>—</span> SEARCH RESULTS
        </div>
        <BookGrid
          books={searchResults.books}
          loggedIn={loggedIn}
          favoriteBooks={favoriteBooks}
          favoriteOnChange={favoriteOnChange}
        />
      </div>
    )
  }

  // Otherwise show recommendations
  const forYou = recommendations?.for_you || [];
  const fromFriends = recommendations?.friend_approved || [];

  const tabItems = [
    {
      key: 'for_you',
      label: (
        <span>
          <HeartOutlined /> For You
          <span style={{ color: '#666', fontSize: 11, marginLeft: 6 }}>({forYou.length})</span>
        </span>
      ),
      children: (
        <BookGrid
          books={forYou}
          loggedIn={loggedIn}
          favoriteBooks={favoriteBooks}
          favoriteOnChange={favoriteOnChange}
        />
      ),
    },
    {
      key: 'friends',
      label: (
        <span>
          <UserOutlined /> From Friends
          <span style={{ color: '#666', fontSize: 11, marginLeft: 6 }}>({fromFriends.length})</span>
        </span>
      ),
      children: (
        <BookGrid
          books={fromFriends}
          loggedIn={loggedIn}
          favoriteBooks={favoriteBooks}
          favoriteOnChange={favoriteOnChange}
        />
      ),
    },
  ]

  return (
    <>
      <style>{`
        .home-tabs .ant-tabs-ink-bar { background: #e63232 !important; }
        .home-tabs .ant-tabs-tab-active .ant-tabs-tab-btn { color: #e63232 !important; }
        .home-tabs .ant-tabs-tab:hover .ant-tabs-tab-btn { color: #e63232 !important; }
        .home-tabs .ant-tabs-tab-btn { color: #888; }
        .home-tabs .ant-tabs-nav::before { border-color: #222 !important; }
      `}</style>

      <div style={{
        color: '#fff',
        fontFamily: "'Bebas Neue', 'Impact', sans-serif",
        fontSize: 22,
        letterSpacing: '0.1em',
        marginBottom: 16,
        display: 'flex',
        alignItems: 'center',
        gap: 10,
      }}>
        <span style={{ color: '#e63232' }}>—</span> RECOMMENDATIONS
      </div>

      <Tabs
        className="home-tabs"
        defaultActiveKey="for_you"
        items={tabItems}
        destroyInactiveTabPane={false}
      />
    </>
  )
}

export default Home;
