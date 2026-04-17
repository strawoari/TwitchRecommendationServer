import { Row, Col, Button } from 'antd'
import Register from './Register'
import Login from './Login'
import Favorites from './Favorites'
import CustomSearch from './CustomSearch'
 
const headerStyle = {
  background: '#0f0f0f',
  borderBottom: '1px solid #1f1f1f',
  padding: '0 32px',
  height: 64,
  display: 'flex',
  alignItems: 'center',
  position: 'sticky',
  top: 0,
  zIndex: 100,
  boxShadow: '0 1px 0 #1a1a1a, 0 4px 24px rgba(0,0,0,0.6)',
}
 
const logoStyle = {
  fontFamily: "'Bebas Neue', 'Impact', sans-serif",
  fontSize: 26,
  letterSpacing: '0.12em',
  color: '#ffffff',
  display: 'flex',
  alignItems: 'center',
  gap: 10,
  userSelect: 'none',
}
 
const accentDot = {
  display: 'inline-block',
  width: 8,
  height: 8,
  borderRadius: '50%',
  background: '#e63232',
  boxShadow: '0 0 8px #e63232aa',
}
 
function PageHeader({ loggedIn, signoutOnClick, signinOnSuccess, favoriteItems, onSearch }) {
  return (
    <div style={headerStyle}>
      <Row align="middle" style={{ width: '100%' }} wrap={false}>

 
        {/* Center — Search bar inline in header */}
        <Col flex="auto" style={{ display: 'flex', justifyContent: 'center', padding: '0 24px' }}>
          <CustomSearch onSuccess={onSearch} headerMode />
        </Col>
 
        {/* Right — Auth */}
        <Col flex="200px" style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            {loggedIn ? (
              <>
                <Favorites favoriteItems={favoriteItems} />
                <Button
                  onClick={signoutOnClick}
                  style={{
                    background: 'transparent',
                    border: '1px solid #333',
                    color: '#aaa',
                    borderRadius: 4,
                    height: 36,
                    padding: '0 20px',
                    fontFamily: "'DM Mono', monospace",
                    fontSize: 12,
                    letterSpacing: '0.08em',
                  }}
                  onMouseEnter={e => {
                    e.currentTarget.style.borderColor = '#e63232'
                    e.currentTarget.style.color = '#e63232'
                  }}
                  onMouseLeave={e => {
                    e.currentTarget.style.borderColor = '#333'
                    e.currentTarget.style.color = '#aaa'
                  }}
                >
                  LOGOUT
                </Button>
              </>
            ) : (
              <>
                <Login onSuccess={signinOnSuccess} />
                <Register />
              </>
            )}
          </div>
        </Col>
      </Row>
    </div>
  )
}
 
export default PageHeader