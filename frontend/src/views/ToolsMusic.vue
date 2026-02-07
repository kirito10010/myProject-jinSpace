<template>
  <div class="music-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-input
        v-model="keyword"
        placeholder="请输入歌曲名、歌手名或专辑名"
        clearable
        @keyup.enter="handleSearch"
        class="search-input"
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
        </template>
      </el-input>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧：搜索结果 -->
      <div class="left-panel">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="10" animated />
        </div>
        
        <div v-else-if="searchResults.length > 0" class="search-results">
          <h3>搜索结果 ({{ totalResults }})</h3>
          <el-scrollbar height="600px">
            <el-list>
              <el-list-item
                v-for="song in searchResults"
                :key="song.id"
                class="song-item"
                @click="selectSong(song)"
                :class="{ active: currentSong?.id === song.id }"
              >
                <template #avatar>
                  <el-image
                    :src="song.pic"
                    fit="cover"
                    class="song-cover"
                    lazy
                  />
                </template>
                <div class="song-info">
                  <div class="song-name">{{ song.name }}</div>
                  <div class="song-artist">{{ song.artist }} - {{ song.album }}</div>
                </div>
                <template #extra>
                  <span class="song-time">{{ formatTime(song.time) }}</span>
                </template>
              </el-list-item>
            </el-list>
          </el-scrollbar>
        </div>
        
        <div v-else-if="searched" class="no-results">
          <el-empty description="未找到相关歌曲" />
        </div>
      </div>

      <!-- 右侧：播放区域 -->
      <div class="right-panel">
        <div v-if="currentSong" class="player-section">
          <!-- 歌曲信息 -->
          <div class="current-song-info">
            <el-image
              :src="currentSong.pic1120 || currentSong.pic"
              fit="cover"
              class="current-song-cover"
            />
            <div class="song-details">
              <h2>{{ currentSong.name }}</h2>
              <p>{{ currentSong.artist }}</p>
              <p class="album">{{ currentSong.album }}</p>
            </div>
          </div>

          <!-- 音频播放器 -->
          <div class="audio-player">
            <audio
              ref="audioRef"
              :src="currentSongUrl"
              controls
              @timeupdate="handleTimeUpdate"
              @loadedmetadata="handleLoadedMetadata"
              @ended="handleEnded"
            />
          </div>

          <!-- 标签页切换区域 -->
          <div class="tab-section">
            <el-tabs v-model="activeTab" type="card" class="custom-tabs">
              <!-- 歌词标签 -->
              <el-tab-pane label="歌词" name="lyric" class="tab-pane">
                <div class="lyric-section">
                  <div v-if="lyrics.length > 0">
                    <el-scrollbar :height="tabContentHeight" class="lyric-scrollbar">
                      <div
                        ref="lyricRef"
                        class="lyric-container"
                      >
                        <div
                          v-for="(lyric, index) in lyrics"
                          :key="index"
                          class="lyric-item"
                          :class="{ active: currentLyricIndex === index }"
                          :data-time="lyric.time"
                        >
                          {{ lyric.lineLyric }}
                        </div>
                      </div>
                    </el-scrollbar>
                  </div>
                  <div v-else class="no-content">
                    <el-empty description="暂无歌词" />
                  </div>
                </div>
              </el-tab-pane>

              <!-- MV标签 -->
              <el-tab-pane label="MV" name="mv" class="tab-pane">
                <div class="mv-section">
                  <div v-if="currentMvUrl">
                    <video
                      :src="currentMvUrl"
                      controls
                      class="mv-player"
                    />
                  </div>
                  <div v-else class="no-content">
                    <el-empty description="暂无MV" />
                  </div>
                </div>
              </el-tab-pane>

              <!-- 评论标签 -->
              <el-tab-pane label="评论" name="comment" class="tab-pane">
                <div class="comment-section">
                  <div v-if="comments.length > 0">
                    <el-scrollbar :height="tabContentHeight">
                      <div class="comment-list">
                        <div
                          v-for="(comment, index) in comments"
                          :key="index"
                          class="comment-item"
                        >
                          <div class="comment-avatar-container">
                            <el-image
                              :src="comment.pic"
                              fit="cover"
                              class="comment-avatar"
                            />
                          </div>
                          <div class="comment-content">
                            <div class="comment-header">
                              <span class="comment-name">{{ comment.name }}</span>
                              <span class="comment-time">{{ comment.time }}</span>
                            </div>
                            <div class="comment-text">{{ comment.msg }}</div>
                          </div>
                        </div>
                      </div>
                    </el-scrollbar>
                  </div>
                  <div v-else class="no-content">
                    <el-empty description="暂无评论" />
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
        
        <div v-else class="no-selection">
          <el-empty description="请选择一首歌曲播放" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import {
  searchMusic,
  getSongUrl,
  getMvUrl,
  getLyric,
  getComment
} from '@/api/music'
import type { SongItem, LyricItem, CommentItem } from '@/api/music'

// 搜索相关
const keyword = ref('')
const searchResults = ref<SongItem[]>([])
const totalResults = ref('0')
const loading = ref(false)
const searched = ref(false)

// 播放相关
const currentSong = ref<SongItem | null>(null)
const currentSongUrl = ref('')
const audioRef = ref<HTMLAudioElement | null>(null)
const currentTime = ref(0)
const duration = ref(0)

// 歌词相关
const lyrics = ref<LyricItem[]>([])
const currentLyricIndex = ref(0)
const lyricRef = ref<HTMLElement | null>(null)

// MV相关
const currentMvUrl = ref('')

// 评论相关
const comments = ref<CommentItem[]>([])

// 标签页相关
const activeTab = ref('lyric')
const tabContentHeight = ref('500px')

// 搜索音乐
const handleSearch = async () => {
  if (!keyword.value.trim()) return
  
  loading.value = true
  searched.value = true
  
  try {
    const response = await searchMusic(keyword.value)
    
    if (response.code === 0) {
      if (response.data && response.data.length > 0) {
        searchResults.value = response.data[0]?.list || []
        totalResults.value = response.data[0]?.total || '0'
      } else {
        searchResults.value = []
        totalResults.value = '0'
      }
    } else {
      searchResults.value = []
      totalResults.value = '0'
    }
  } catch (error: any) {
    searchResults.value = []
    totalResults.value = '0'
  } finally {
    loading.value = false
  }
}

// 选择歌曲
const selectSong = async (song: SongItem) => {
  currentSong.value = song
  
  // 清空之前的MV和评论
  currentMvUrl.value = ''
  comments.value = []
  
  // 获取歌曲播放地址
  try {
    const songResponse = await getSongUrl(song.id)
    if (songResponse.code === 0 && songResponse.data && Array.isArray(songResponse.data) && songResponse.data.length > 0) {
      const firstItem = songResponse.data[0]
      if (firstItem && typeof firstItem.url === 'string') {
        currentSongUrl.value = firstItem.url
        // 自动播放
        audioRef.value?.play().catch(() => {
          // 自动播放失败，忽略错误
        })
      } else {
        currentSongUrl.value = ''
      }
    } else {
      currentSongUrl.value = ''
    }
  } catch (error) {
    currentSongUrl.value = ''
  }
  
  // 获取歌词
  try {
    const lyricResponse = await getLyric(song.id)
    if (lyricResponse.code === 0 && lyricResponse.data && Array.isArray(lyricResponse.data)) {
      // 过滤掉无效的歌词项，确保每个项都有必要的属性
      lyrics.value = lyricResponse.data.filter((item: any) => {
        return item && typeof item.lineLyric === 'string' && typeof item.time === 'string'
      })
      currentLyricIndex.value = 0
    } else {
      lyrics.value = []
    }
  } catch (error) {
    lyrics.value = []
  }
  
  // 获取MV
  try {
    const mvResponse = await getMvUrl(song.id)
    if (mvResponse.code === 0 && mvResponse.data && Array.isArray(mvResponse.data) && mvResponse.data.length > 0) {
      const firstItem = mvResponse.data[0]
      if (firstItem && typeof firstItem.url === 'string') {
        currentMvUrl.value = firstItem.url
      } else {
        currentMvUrl.value = ''
      }
    } else {
      currentMvUrl.value = ''
    }
  } catch (error) {
    currentMvUrl.value = ''
  }
  
  // 获取评论
  try {
    const commentResponse = await getComment(song.id)
    if (commentResponse.code === 0 && commentResponse.data) {
      // 过滤掉null值，确保评论数组只包含有效评论
      comments.value = commentResponse.data.filter((comment: any) => comment !== null && comment !== undefined)
    } else {
      comments.value = []
    }
  } catch (error) {
    comments.value = []
  }
}

// 格式化时间
const formatTime = (timeStr: string) => {
  const time = parseInt(timeStr)
  const minutes = Math.floor(time / 60)
  const seconds = time % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 处理时间更新
const handleTimeUpdate = () => {
  if (audioRef.value) {
    currentTime.value = audioRef.value.currentTime
    updateCurrentLyric()
  }
}

// 处理加载完成
const handleLoadedMetadata = () => {
  if (audioRef.value) {
    duration.value = audioRef.value.duration
  }
}

// 处理播放结束
const handleEnded = () => {
  currentLyricIndex.value = 0
}

// 更新当前歌词
const updateCurrentLyric = () => {
  if (lyrics.value.length === 0) return
  
  // 找到当前时间对应的歌词索引
  const currentTimeMs = currentTime.value * 1000
  let newIndex = 0
  
  for (let i = 0; i < lyrics.value.length; i++) {
    const lyricItem = lyrics.value[i]
    if (lyricItem && typeof lyricItem.time === 'string') {
      // 解析时间格式 "00:00.000" 为毫秒
      const timeStr = lyricItem.time
      const parts = timeStr.split(':')
      if (parts.length === 2 && typeof parts[0] === 'string' && typeof parts[1] === 'string') {
        const minutes = parseInt(parts[0], 10) || 0
        const secondsParts = parts[1].split('.')
        const seconds = secondsParts[0] ? parseInt(secondsParts[0], 10) || 0 : 0
        const milliseconds = secondsParts[1] ? parseInt(secondsParts[1], 10) || 0 : 0
        const lyricTime = minutes * 60 * 1000 + seconds * 1000 + milliseconds
        
        if (lyricTime <= currentTimeMs) {
          newIndex = i
        } else {
          break
        }
      }
    }
  }
  
  if (newIndex !== currentLyricIndex.value) {
    currentLyricIndex.value = newIndex
    scrollToCurrentLyric()
  }
}

// 滚动到当前歌词
const scrollToCurrentLyric = () => {
  if (!lyricRef.value) return
  
  const lyricElements = lyricRef.value.querySelectorAll('.lyric-item')
  const currentElement = lyricElements[currentLyricIndex.value]
  
  if (currentElement) {
    const container = lyricRef.value.parentElement as HTMLElement
    const containerHeight = container.clientHeight
    const elementTop = (currentElement as HTMLElement).offsetTop
    const elementHeight = (currentElement as HTMLElement).clientHeight
    
    container.scrollTop = elementTop - containerHeight / 2 + elementHeight / 2
  }
}

// 监听歌词变化，重置索引
watch(lyrics, () => {
  currentLyricIndex.value = 0
})
</script>

<style scoped>
.music-container {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.search-section {
  padding: 20px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  z-index: 10;
}

.search-input {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.main-content {
  display: flex;
  gap: 20px;
  flex: 1;
  padding: 20px;
  overflow: hidden;
}

.left-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 24px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.right-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 24px;
  overflow-y: auto;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.player-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.search-results h3 {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-results h3::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #409eff 0%, #69c0ff 100%);
  border-radius: 2px;
}

/* 优化 el-list 样式 */
.el-list {
  width: 100%;
}

.el-list-item {
  width: 100%;
  margin-bottom: 12px;
  border-radius: 10px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 优化歌曲项样式 */
.song-item {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 10px;
  padding: 16px;
  background: #f9f9f9;
  border: 1px solid #ebeef5;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 16px;
}

.song-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: transparent;
  transition: all 0.3s;
}

.song-item:hover {
  background: #f0f9ff;
  border-color: #d6ecff;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.15);
}

.song-item.active {
  background: #ecf5ff;
  border-color: #c6e2ff;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.2);
}

.song-item.active::before {
  background: linear-gradient(180deg, #409eff 0%, #69c0ff 100%);
}

/* 优化封面图片显示 */
.song-cover {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
  flex-shrink: 0;
}

.song-item:hover .song-cover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 优化歌曲信息 */
.song-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 优化歌曲时间显示 */
.song-time {
  font-size: 14px;
  color: #909399;
  font-family: 'Courier New', monospace;
  flex-shrink: 0;
  margin-left: 16px;
}

/* 修复 el-list-item 的默认样式影响 */
.el-list-item__content {
  width: 100%;
  display: flex;
  align-items: center;
}

.el-list-item__avatar {
  margin-right: 0 !important;
}

.el-list-item__extra {
  margin-left: auto !important;
  padding-left: 16px;
}

.song-name {
  font-weight: 600;
  font-size: 16px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133;
  transition: all 0.3s;
}

.song-item:hover .song-name {
  color: #409eff;
}

.song-artist {
  font-size: 14px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.3s;
}

.song-time {
  font-size: 14px;
  color: #909399;
  font-family: 'Courier New', monospace;
}

.loading-container {
  padding: 40px 0;
}

.no-results,
.no-selection {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  flex-direction: column;
  gap: 20px;
}

.player-section {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.current-song-info {
  display: flex;
  gap: 24px;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
  transition: all 0.3s;
}

.current-song-info:hover {
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.current-song-cover {
  width: 160px;
  height: 160px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  object-fit: cover;
}

.current-song-info:hover .current-song-cover {
  transform: scale(1.05) rotate(2deg);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.25);
}

.song-details {
  flex: 1;
}

.song-details h2 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.song-details p {
  margin: 8px 0;
  color: #606266;
  font-size: 16px;
}

.song-details .album {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 8px;
}

.song-details .album::before {
  content: '💽';
  font-size: 16px;
}

.audio-player {
  padding: 20px;
  background: #f9f9f9;
  border-radius: 12px;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.05);
}

.audio-player audio {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.audio-player audio:hover {
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.lyric-section,
.mv-section,
.comment-section {
  flex: 1;
  min-height: 200px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.lyric-section:hover,
.mv-section:hover,
.comment-section:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.lyric-section h3,
.mv-section h3,
.comment-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.lyric-section h3::before,
.mv-section h3::before,
.comment-section h3::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #409eff 0%, #69c0ff 100%);
  border-radius: 2px;
}

.lyric-section h3 {
  color: #409eff;
}

.mv-section h3 {
  color: #67c23a;
}

.comment-section h3 {
  color: #e6a23c;
}

.lyric-section h3::before {
  background: linear-gradient(180deg, #409eff 0%, #69c0ff 100%);
}

.mv-section h3::before {
  background: linear-gradient(180deg, #67c23a 0%, #85ce61 100%);
}

.comment-section h3::before {
  background: linear-gradient(180deg, #e6a23c 0%, #ebb563 100%);
}

.lyric-scrollbar {
  flex: 1;
  overflow: auto;
}

.lyric-container {
  padding: 24px 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.lyric-item {
  text-align: center;
  padding: 12px 20px;
  font-size: 16px;
  color: #606266;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  margin: 0 20px;
  position: relative;
}

.lyric-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent 0%, #409eff 50%, transparent 100%);
  transition: all 0.4s;
}

.lyric-item.active {
  color: #409eff;
  font-size: 18px;
  font-weight: 600;
  background: rgba(64, 158, 255, 0.05);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
  transform: translateY(-2px);
}

.lyric-item.active::after {
  width: 80%;
}

.mv-player {
  width: 100%;
  height: 320px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s;
}

.mv-player:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
  transform: scale(1.02);
}

.comment-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
  object-fit: cover;
  border: 3px solid #fff;
}

.comment-item:hover .comment-avatar {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3);
  border-color: #409eff;
}

.comment-content {
  flex: 1;
  min-width: 0;
  margin-left: 16px;
}



/* 评论列表样式 */
.comment-list {
  width: 100%;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 优化评论项样式 */
.comment-item {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 24px;
  border-radius: 12px;
  background: #f9f9f9;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
  width: 100%;
  box-sizing: border-box;
  display: flex;
  gap: 16px;
  align-items: flex-start;
  position: relative;
  overflow: hidden;
}

.comment-avatar-container {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-name {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 8px;
  border-radius: 10px;
}

.comment-text {
  font-size: 15px;
  color: #606266;
  line-height: 1.7;
  word-break: break-word;
  background: #ffffff;
  padding: 16px;
  border-radius: 12px;
  border-left: 4px solid #e4e7ed;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.comment-item:hover .comment-text {
  border-left-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15);
  transform: translateX(4px);
}

/* 滚动条样式优化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
  transition: all 0.3s;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
    height: auto;
  }
  
  .left-panel,
  .right-panel {
    width: 100%;
    margin-bottom: 20px;
  }
  
  .current-song-info {
    flex-direction: column;
    text-align: center;
  }
  
  .current-song-cover {
    width: 120px;
    height: 120px;
  }
  
  .song-details h2 {
    font-size: 20px;
  }
}

/* 标签页相关样式 */
.tab-section {
  margin-top: 20px;
  flex: 1;
  min-height: 500px;
  display: flex;
  flex-direction: column;
}

.custom-tabs {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.custom-tabs .el-tabs__header {
  background: linear-gradient(135deg, #f9f9f9 0%, #f0f0f0 100%);
  border-bottom: 1px solid #ebeef5;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
}

.custom-tabs .el-tabs__nav {
  margin: 0;
  height: 64px;
  display: flex;
  align-items: center;
}

.custom-tabs .el-tabs__tab {
  font-size: 16px;
  font-weight: 500;
  padding: 0 32px;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #606266;
  margin: 0 8px;
  border-radius: 24px;
  position: relative;
  overflow: hidden;
}

.custom-tabs .el-tabs__tab::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent 0%, rgba(64, 158, 255, 0.1) 100%);
  transition: all 0.4s;
}

.custom-tabs .el-tabs__tab:hover::before {
  left: 100%;
}

.custom-tabs .el-tabs__tab:hover {
  color: #409eff;
  background: rgba(64, 158, 255, 0.05);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.custom-tabs .el-tabs__tab.is-active {
  color: #fff;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #69c0ff 100%);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
  transform: translateY(-2px);
}

.custom-tabs .el-tabs__active-bar {
  display: none;
}

.tab-pane {
  padding: 24px;
  background: #fff;
  min-height: 450px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.no-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: #f9f9f9;
  border-radius: 12px;
  margin: 20px 0;
  flex: 1;
}

/* 优化各区域样式 */
.lyric-section,
.mv-section,
.comment-section {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.lyric-scrollbar {
  flex: 1;
  overflow: auto;
}

.mv-player {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
}

.mv-player:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  transform: scale(1.02);
}

/* 优化歌词容器 */
.lyric-container {
  padding: 32px 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
  flex: 1;
}

.lyric-item {
  text-align: center;
  padding: 16px 24px;
  font-size: 16px;
  color: #606266;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  margin: 0 24px;
  position: relative;
}

.lyric-item.active {
  color: #409eff;
  font-size: 18px;
  font-weight: 600;
  background: rgba(64, 158, 255, 0.1);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);
  transform: translateY(-2px);
}

/* 优化评论区域 */
.comment-item {
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  background: #f9f9f9;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.comment-item:hover {
  background: #f0f9ff;
  border-color: #d6ecff;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.25);
  transform: translateY(-4px);
  border-left: 4px solid #409eff;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.song-item {
  animation: fadeInUp 0.5s ease-out;
  animation-fill-mode: both;
}

.song-item:nth-child(1) { animation-delay: 0.1s; }
.song-item:nth-child(2) { animation-delay: 0.2s; }
.song-item:nth-child(3) { animation-delay: 0.3s; }
.song-item:nth-child(4) { animation-delay: 0.4s; }
.song-item:nth-child(5) { animation-delay: 0.5s; }
.song-item:nth-child(6) { animation-delay: 0.6s; }
.song-item:nth-child(7) { animation-delay: 0.7s; }
.song-item:nth-child(8) { animation-delay: 0.8s; }
.song-item:nth-child(9) { animation-delay: 0.9s; }
.song-item:nth-child(10) { animation-delay: 1s; }
</style>