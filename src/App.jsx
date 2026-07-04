import { useState, useEffect } from 'react'
import { logout, getFavoriteItem, getRecommendations } from './utils'
import PageHeader from './pages/PageHeader';
import Home from './pages/Home';
import './index.css';
import { Layout, message, Spin } from 'antd'
import { BookOutlined } from '@ant-design/icons';

const { Header, Content } = Layout

function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [favoriteItems, setFavoriteItems] = useState([])
  const [recommendations, setRecommendations] = useState({ for_you: [], friend_approved: [] })
  const [searchResults, setSearchResults] = useState(null)
  const [loading, setLoading] = useState(true)

  // Load default recommendations on mount
  useEffect(() => {
    setLoading(true);
    getRecommendations()
      .then((data) => {
        setRecommendations(data || { for_you: [], friend_approved: [] });
        setSearchResults(null);
      })
      .catch((err) => message.error(err.message))
      .finally(() => setLoading(false));
  }, [])

  const signinOnSuccess = () => {
    setLoggedIn(true);
    // Load personalized recommendations on login
    setLoading(true);
    getRecommendations()
      .then((data) => {
        setRecommendations(data || { for_you: [], friend_approved: [] });
        setSearchResults(null);
      })
      .catch((err) => message.error(err.message))
      .finally(() => setLoading(false));

    getFavoriteItem()
      .then((data) => setFavoriteItems(Array.isArray(data) ? data : []))
      .catch((err) => message.error(err.message));
  }

  const signoutOnClick = () => {
    logout()
      .then(() => {
        setLoggedIn(false);
        setFavoriteItems([]);
        message.success('Successfully Signed out');
        // Revert to default recommendations on logout
        setLoading(true);
        return getRecommendations()
          .then((data) => {
            setRecommendations(data || { for_you: [], friend_approved: [] });
            setSearchResults(null);
          });
      })
      .catch((err) => message.error(err.message))
      .finally(() => setLoading(false));
  }

  const favoriteOnChange = () => {
    getFavoriteItem()
      .then((data) => setFavoriteItems(Array.isArray(data) ? data : []))
      .catch((err) => message.error(err.message));
  };

  const onSearch = (results) => {
    setSearchResults(results);
  };

  const onClearSearch = () => {
    setSearchResults(null);
  };

  return (
    <Layout style={{ minHeight: '100vh', background: '#0a0a0a' }}>
      <Header style={{ padding: 0, height: 'auto', lineHeight: 'normal', background: 'transparent' }}>
        <PageHeader
          loggedIn={loggedIn}
          signoutOnClick={signoutOnClick}
          signinOnSuccess={signinOnSuccess}
          favoriteItems={favoriteItems}
          onSearch={onSearch}
          onClearSearch={onClearSearch}
        />
      </Header>
      <Layout style={{ padding: '24px 32px', background: '#0a0a0a' }}>
        <Content
          style={{
            padding: 32,
            margin: 0,
            minHeight: 600,
            background: '#111111',
            borderRadius: 8,
            border: '1px solid #1f1f1f',
            color: '#e0e0e0',
            fontFamily: "'DM Sans', sans-serif",
          }}
        >
          {loading ? (
            <div style={{ textAlign: 'center', padding: 80 }}>
              <Spin size="large" />
              <div style={{ color: '#555', marginTop: 16, fontFamily: "'DM Mono', monospace", fontSize: 12 }}>
                Loading recommendations...
              </div>
            </div>
          ) : (
            <Home
              searchResults={searchResults}
              recommendations={recommendations}
              loggedIn={loggedIn}
              favoriteBooks={favoriteItems}
              favoriteOnChange={favoriteOnChange}
            />
          )}
        </Content>
      </Layout>
    </Layout>
  )
}

export default App