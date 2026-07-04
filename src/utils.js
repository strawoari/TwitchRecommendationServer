const SERVER_ORIGIN = '/api';

// ─── Authentication ────────────────────────────────────────────────

const loginUrl = `${SERVER_ORIGIN}/login`;

export const login = (credential) => {
  const formData = new FormData();
  formData.append("username", credential.username);
  formData.append("password", credential.password);

  return fetch(loginUrl, {
    method: 'POST',
    credentials: 'include',
    body: formData
  }).then((response) => {
    if (response.status !== 204) {
      throw Error('Fail to log in');
    }
  })
}

const registerUrl = `${SERVER_ORIGIN}/register`;

export const register = (data) => {
  return fetch(registerUrl, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data)
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to register');
    }
  })
}

const logoutUrl = `${SERVER_ORIGIN}/logout`;

export const logout = () => {
  return fetch(logoutUrl, {
    method: 'POST',
    credentials: 'include',
  }).then((response) => {
    if (response.status !== 204) {
      throw Error('Fail to log out');
    }
  })
}

// ─── Book Search ───────────────────────────────────────────────────

/**
 * Search books by query string with optional pagination.
 * Backend: GET /api/books/search?query=...&page=...
 * Returns: { books: BookWebDto[] }
 */
export const searchBooks = (query, page = 1) => {
  const params = new URLSearchParams();
  params.append('query', query);
  if (page) params.append('page', page);

  return fetch(`${SERVER_ORIGIN}/books/search?${params.toString()}`, {
    credentials: 'include',
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to search books');
    }
    return response.json();
  })
}

// ─── Recommendations ───────────────────────────────────────────────

/**
 * Fetch personalized book recommendations.
 * Backend: GET /feed
 * Returns: { friend_approved: BookWebDto[], for_you: BookWebDto[] }
 */
export const getRecommendations = () => {
  return fetch('/feed', {
    credentials: 'include',
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get recommendations');
    }
    return response.json();
  })
}

// ─── Favorites ─────────────────────────────────────────────────────

const favoriteItemUrl = `${SERVER_ORIGIN}/favorite`;

export const addFavoriteItem = (favItem) => {
  return fetch(favoriteItemUrl, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ favorite: favItem })
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to add favorite item');
    }
  })
}

export const deleteFavoriteItem = (favItem) => {
  return fetch(favoriteItemUrl, {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ favorite: favItem })
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to delete favorite item');
    }
  })
}

export const getFavoriteItem = () => {
  return fetch(favoriteItemUrl, {
    credentials: 'include',
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get favorite item');
    }
    return response.json();
  })
}

// ─── Helpers ───────────────────────────────────────────────────────

/**
 * Returns the Gutenberg.org URL for a book by its ID.
 */
export const getBookUrl = (book) => {
  if (book?.gutenbergId) {
    return `https://www.gutenberg.org/ebooks/${book.gutenbergId}`;
  }
  return '#';
}
