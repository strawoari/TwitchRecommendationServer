import { useState, useEffect, useMemo } from 'react'
import {
  logout, getFavoriteItem, getTopGames,
  searchGameById, getRecommendations, getResourcesForTopGames
} from './utils'
import PageHeader from './pages/PageHeader';
import Home from './pages/Home';
import './index.css';
import { FireOutlined } from '@ant-design/icons';
import { Layout, message, Menu } from 'antd'
const { Header, Sider, Content } = Layout
 
function App() {
  const [loggedIn, setLoggedIn] = useState(false)
  const [favoriteItems, setFavoriteItems] = useState({ videos: [], streams: []})
  const [topGames, setTopGames] = useState([])
  const [resources, setResources] = useState({ videos: [], streams: []})
 
  // Load top games for sidebar, then auto-populate home with top 5 games' content
  useEffect(() => {
    getTopGames()
      .then((data) => {
        setTopGames(data);
        const top5Ids = data.slice(0, 5).map((g) => g.id);
        return getResourcesForTopGames(top5Ids, 10);
      })
      .then((data) => setResources(data))
      .catch((err) => message.error(err.message));
  }, [])
 
  const signinOnSuccess = () => {
    setLoggedIn(true);
    // Swap home content to personalized recommendations on login
    getRecommendations()
      .then((data) => setResources(data))
      .catch((err) => message.error(err.message));
 
    getFavoriteItem()
      .then((data) => setFavoriteItems(data))
      .catch((err) => message.error(err.message));
  }
 
  const signoutOnClick = () => {
    logout()
      .then(() => {
        setLoggedIn(false);
        setFavoriteItems({ videos: [], streams: []});
        message.success('Successfully Signed out');
        // Revert to top 5 games content on logout
        const top5Ids = topGames.slice(0, 5).map((g) => g.id);
        return getResourcesForTopGames(top5Ids, 10);
      })
      .then((data) => setResources(data))
      .catch((err) => message.error(err.message));
  }
 
  const favoriteOnChange = () => {
    getFavoriteItem()
      .then((data) => setFavoriteItems(data))
      .catch((err) => message.error(err.message));
  };
 
  const onGameSelect = ({ key }) => {
    searchGameById(key)
      .then((data) => setResources(data))
      .catch((err) => message.error(err.message));
  };
 
  const sidebarItems = useMemo(() => [
    {
      label: <span style={{ color: 'white' }}>Popular Games</span>,
      key: "popular_games",
      icon: <FireOutlined color = 'red'/>,
      children: topGames.map((game) => ({
        label: <span style={{ color: 'grey' }}>{game.name}</span>,
        key: String(game.id),
        icon: (
          <img
            alt={game.name}
            src={game.box_art_url.replace('{height}', '40').replace('{width}', '40')}
            style={{ borderRadius: '50%', marginRight: '10px', width: 28, height: 28 }}
          />
        )
      }))
    }
  ], [topGames])
 
  return (
    <Layout style={{ minHeight: '100vh', background: '#0a0a0a' }}>
      <Header style={{ padding: 0, height: 'auto', lineHeight: 'normal', background: 'transparent' }}>
        <PageHeader
          loggedIn={loggedIn}
          signoutOnClick={signoutOnClick}
          signinOnSuccess={signinOnSuccess}
          favoriteItems={favoriteItems}
          onSearch={(data) => setResources(data)}
        />
      </Header>
      <Layout style={{ background: '#0a0a0a' }}>
        <Sider
          width={260}
          style={{
            background: '#111111',
            borderRight: '1px solid #3e3e3e',
            overflow: 'auto',
            height: 'calc(100vh - 64px)',
            position: 'sticky',
            top: 64,
          }}
        >
          <Menu
            mode="inline"
            onSelect={onGameSelect}
            defaultOpenKeys={['popular_games']}
            style={{
              background: '#111111',
              borderRight: 'none',
              '--ant-color-primary': '#da3434',
              '--ant-menu-item-selected-color': '#e63232',
              '--ant-menu-item-selected-bg': 'rgba(230,50,50,0.08)',
            }}
            items={sidebarItems}
          />
        </Sider>
        <Layout style={{ padding: '24px', background: '#0a0a0a' }}>
          <Content
            style={{
              padding: 32,
              margin: 0,
              minHeight: 600,
              background: '#111111',
              borderRadius: 4,
              border: '1px solid #1f1f1f',
              color: '#e0e0e0',
              fontFamily: "'DM Sans', sans-serif",
            }}
          >
            <Home
              resources={resources}
              loggedIn={loggedIn}
              favoriteOnChange={favoriteOnChange}
              favoriteItems={favoriteItems}
            />
          </Content>
        </Layout>
      </Layout>
    </Layout>
  )
}
 
export default App