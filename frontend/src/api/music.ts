import service from './request'

// API密钥
const API_KEY = 'e6748b1e2aba0ac10737b216b3960c71'

// 搜索结果项接口
export interface SongItem {
  id: string
  name: string
  artist: string
  album: string
  time: string
  pic: string
  pic1120: string
}

// 搜索结果接口
export interface SearchResult {
  total: string
  list: SongItem[]
}

// 搜索响应接口
export interface SearchResponse {
  code: number
  msg: string
  data: SearchResult[]
}

// 歌曲播放地址响应接口
export interface SongUrlResponse {
  code: number
  msg: string
  data: {
    id: string
    url: string
  }[]
}

// MV播放地址响应接口
export interface MvUrlResponse {
  code: number
  msg: string
  data: {
    id: string
    url: string
  }[]
}

// 歌词项接口
export interface LyricItem {
  id: string
  lineLyric: string
  time: string
}

// 歌词响应接口
export interface LyricResponse {
  code: number
  msg: string
  data: LyricItem[]
}

// 评论项接口
export interface CommentItem {
  name: string
  pic: string
  msg: string
  time: string
}

// 评论响应接口
export interface CommentResponse {
  code: number
  msg: string
  data: CommentItem[]
}

// 搜索音乐
export const searchMusic = (keyword: string, num: number = 15) => {
  return service.get<SearchResponse>('/music/api.php', {
    params: {
      api: '10',
      key: API_KEY,
      search: keyword,
      type: 'so',
      num
    }
  })
}

// 获取歌曲播放地址
export const getSongUrl = (songId: string) => {
  return service.get<SongUrlResponse>('/music/api.php', {
    params: {
      api: '10',
      key: API_KEY,
      search: songId,
      type: 'song'
    }
  })
}

// 获取MV播放地址
export const getMvUrl = (songId: string) => {
  return service.get<MvUrlResponse>('/music/api.php', {
    params: {
      api: '10',
      key: API_KEY,
      search: songId,
      type: 'mv'
    }
  })
}

// 获取歌词
export const getLyric = (songId: string) => {
  return service.get<LyricResponse>('/music/api.php', {
    params: {
      api: '10',
      key: API_KEY,
      search: songId,
      type: 'lrc'
    }
  })
}

// 获取评论
export const getComment = (songId: string) => {
  return service.get<CommentResponse>('/music/api.php', {
    params: {
      api: '10',
      key: API_KEY,
      search: songId,
      type: 'comment'
    }
  })
}
