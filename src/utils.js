const SERVER_ORIGIN = '/api';

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

const topGamesUrl = `${SERVER_ORIGIN}/game`;

export const getTopGames = () => {
  return fetch(topGamesUrl).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get top games');
    }
    return response.json();
  })
}

const getGameDetailsUrl = `${SERVER_ORIGIN}/game?game_name=`;

const getGameDetails = (gameName) => {
  return fetch(`${getGameDetailsUrl}${encodeURIComponent(gameName)}`).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to find the game');
    }
    return response.json();
  });
}

const searchGameByIdUrl = `${SERVER_ORIGIN}/search?game_id=`;

export const searchGameById = (gameId) => {
  return fetch(`${searchGameByIdUrl}${gameId}`).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to find the game');
    }
    return response.json();
  })
}

export const searchGameByName = (gameName) => {
  return getGameDetails(gameName).then((data) => {
    const game = Array.isArray(data) ? data[0] : data;
    if (game && game.id) {
      return searchGameById(game.id);
    }
    throw Error('Fail to find the game')
  })
}

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

const getRecommendedItemsUrl = `${SERVER_ORIGIN}/recommendation`;

/**
 * Fetch personalized recommendations for a logged-in user.
 * Optionally pass CCL labels to exclude (e.g. ['SexualThemes', 'Gambling']).
 */
export const getRecommendations = (cclExclusions = []) => {
  const params = new URLSearchParams();
  cclExclusions.forEach((label) => params.append('cclExclusions', label));
  const url = cclExclusions.length
    ? `${getRecommendedItemsUrl}?${params.toString()}`
    : getRecommendedItemsUrl;

  return fetch(url, {
    credentials: 'include',
  }).then((response) => {
    if (response.status !== 200) {
      throw Error('Fail to get recommended item');
    }
    return response.json();
  })
}

export const getResourcesForTopGames = (gameIds, limitEach = 10) => {
  return Promise.allSettled(gameIds.map((id) => searchGameById(id)))
    .then((results) => {
      const merged = { streams: [], videos: []};
      results.forEach((result) => {
        if (result.status === 'fulfilled') {
          const r = result.value;
          if (r.streams) merged.streams.push(...r.streams);
          if (r.videos)  merged.videos.push(...r.videos);
        }
      });
      return {
        streams: merged.streams.slice(0, limitEach),
        videos:  merged.videos.slice(0, limitEach),
      };
    });
};

/**
 * Resolves the correct external URL for any item type.
 * - STREAM: constructed from broadcaster_name since the API doesn't return a url field
 * - VIDEO / CLIP: use the url field directly
 */
export const getItemUrl = (item) => {
  if (item.item_type === 'STREAM') {
    // broadcaster_name may be display-formatted (e.g. "OW_ESPORTS_JP"),
    // Twitch channel URLs are always lowercase
    return `https://www.twitch.tv/${item.broadcaster_name.toLowerCase()}`;
  }
  return item.url || '#';
};