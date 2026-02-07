<template>
  <div class="weather-container">
    <div class="weather-card">
      <div class="card-header">
        <el-icon><Sunny /></el-icon>
        <span>天气查询</span>
      </div>
      
      <div class="weather-content">
        <!-- 城市选择 -->
        <el-form :inline="true" class="city-select-form">
          <el-form-item label="省份">
            <el-select 
              v-model="selectedProvince" 
              placeholder="请选择省份" 
              @change="handleProvinceChange"
              class="select-item"
              value-key="id"
            >
              <el-option 
                v-for="province in provinces" 
                :key="province.id" 
                :label="province.name" 
                :value="province"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="城市">
            <el-select 
              v-model="selectedCity" 
              placeholder="请选择城市" 
              class="select-item"
              :disabled="!selectedProvince"
              value-key="id"
            >
              <el-option 
                v-for="city in cities" 
                :key="city.id" 
                :label="city.name" 
                :value="city"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="getWeather" :disabled="!selectedCity">查询天气</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 加载状态 -->
        <div class="loading-state" v-if="loading">
          <el-skeleton :rows="6" animated />
        </div>
        
        <!-- 天气结果展示 -->
        <div class="weather-result" v-else-if="weatherData && weatherData.data && weatherData.data[0]">
          <!-- 当前天气概览 -->
          <div class="weather-overview-card">
            <div class="weather-overview">
              <h2>{{ weatherData.data[0].city }} 天气</h2>
              <div class="update-time">更新时间：{{ weatherData.data[0].time }}</div>
              <div class="current-weather" v-if="weatherData.data[0].yesterday">
                <div class="weather-type">{{ weatherData.data[0].yesterday.type }}</div>
                <div class="weather-temp">{{ weatherData.data[0].yesterday.high }} / {{ weatherData.data[0].yesterday.low }}</div>
                <div class="weather-info">
                  <div>风向：{{ weatherData.data[0].yesterday.fx }} {{ weatherData.data[0].yesterday.fl }}</div>
                  <div>空气质量：{{ weatherData.data[0].yesterday.aqi }}</div>
                  <div>{{ weatherData.data[0].yesterday.notice }}</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 未来天气预报 -->
          <div class="weather-forecast-card">
            <div class="forecast-header">
              <span>未来15天天气预报</span>
            </div>
            
            <div class="forecast-list" v-if="weatherData.data[0].weather">
              <div class="forecast-item" v-for="(item, index) in weatherData.data[0].weather" :key="index">
                <div class="forecast-date">{{ item.ymd }}</div>
                <div class="forecast-week">{{ item.week }}</div>
                <div class="forecast-type">{{ item.type }}</div>
                <div class="forecast-temp">{{ item.low }} / {{ item.high }}</div>
                <div class="forecast-info">
                  <div>{{ item.fx }} {{ item.fl }}</div>
                  <div>AQI: {{ item.aqi }}</div>
                </div>
                <div class="forecast-notice">{{ item.notice }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div class="weather-empty" v-else>
          <el-empty description="请选择城市查询天气" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Sunny } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 城市数据
const provinces = ref<any[]>([])
const cities = ref<any[]>([])

// 选中的城市
const selectedProvince = ref<any>(null)
const selectedCity = ref<any>(null)

// 天气数据
const weatherData = ref<any>(null)
const loading = ref<boolean>(false)

// 获取省份列表
const getProvinces = async () => {
  try {
    const response = await fetch('/api/cities/provinces')
    const data = await response.json()
    provinces.value = data
  } catch (error) {
    ElMessage.error('获取省份列表失败')
    console.error('获取省份列表失败:', error)
  }
}

// 获取城市列表
const getCities = async (provinceId: any) => {
  try {
    const response = await fetch(`/api/cities/cities/${provinceId}`)
    const data = await response.json()
    cities.value = data
  } catch (error) {
    ElMessage.error('获取城市列表失败')
    console.error('获取城市列表失败:', error)
  }
}

// 省份选择变化
const handleProvinceChange = () => {
  selectedCity.value = null
  cities.value = []
  
  if (selectedProvince.value) {
    getCities(selectedProvince.value.id)
  }
}

// 查询天气
const getWeather = async () => {
  if (!selectedCity.value) {
    ElMessage.warning('请选择城市')
    return
  }
  
  loading.value = true
  weatherData.value = null
  
  try {
    const cityName = selectedCity.value.name
    const response = await fetch(`/api/weather?cityName=${encodeURIComponent(cityName)}`)
    const data = await response.json()
    
    if (data.code === 0) {
      weatherData.value = data
    } else {
      ElMessage.error(data.msg || '查询天气失败')
    }
  } catch (error) {
    ElMessage.error('查询天气失败')
    console.error('查询天气失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化获取省份列表
onMounted(() => {
  getProvinces()
})
</script>

<style scoped>
.weather-container {
  padding: 20px;
  min-height: 100vh;
  background: #ffffff;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.weather-card {
  width: 100%;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: bold;
  font-size: 18px;
  color: #333;
  padding: 20px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.card-header .el-icon {
  font-size: 20px;
  color: #409eff;
}

.weather-content {
  padding: 24px;
}

/* 城市选择表单 */
.city-select-form {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

.select-item {
  width: 160px;
}

.select-item .el-select {
  width: 100%;
}

.select-item .el-select__wrapper {
  border-radius: 8px;
  transition: all 0.3s;
  border: 1px solid #dee2e6;
}

.select-item .el-select__wrapper:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.select-item .el-select__wrapper.is-focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.city-select-form .el-button {
  padding: 0 24px;
  height: 36px;
  border-radius: 8px;
  font-weight: 500;
  background: #409eff;
  border: none;
  transition: all 0.3s;
}

.city-select-form .el-button:hover {
  background: #66b1ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.city-select-form .el-button:disabled {
  background: #e0e0e0;
  box-shadow: none;
}

/* 天气结果展示 */
.weather-result {
  margin-top: 24px;
}

/* 天气概览卡片 */
.weather-overview-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  transition: all 0.3s;
}

.weather-overview-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.weather-overview {
  text-align: center;
  padding: 32px;
  background: #f8f9fa;
}

.weather-overview h2 {
  margin-bottom: 12px;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.update-time {
  font-size: 14px;
  color: #6c757d;
  margin-bottom: 24px;
  font-style: italic;
}

.current-weather {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.weather-type {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
  animation: pulse 2s infinite;
}

.weather-temp {
  font-size: 28px;
  color: #333;
  font-weight: 600;
}

.weather-info {
  font-size: 14px;
  color: #6c757d;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 12px;
  background: #ffffff;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

/* 天气预报卡片 */
.weather-forecast-card {
  margin-top: 24px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
}

.forecast-header {
  font-weight: bold;
  font-size: 16px;
  color: #333;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.forecast-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 20px;
  padding: 0 20px 20px;
}

.forecast-item {
  background: #ffffff;
  padding: 20px;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  position: relative;
  overflow: hidden;
}

.forecast-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border-color: #409eff;
  transform: translateY(-2px);
}

.forecast-date {
  font-weight: 600;
  font-size: 15px;
  color: #333;
  margin-bottom: 6px;
}

.forecast-week {
  font-size: 13px;
  color: #6c757d;
  margin-bottom: 12px;
  background: #e3f2fd;
  padding: 3px 10px;
  border-radius: 10px;
  display: inline-block;
  color: #1976d2;
}

.forecast-type {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 10px;
}

.forecast-temp {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 500;
}

.forecast-info {
  font-size: 13px;
  color: #6c757d;
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin-bottom: 10px;
  background: #f8f9fa;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.forecast-notice {
  font-size: 12px;
  color: #f56c6c;
  font-style: italic;
  background: #fef0f0;
  padding: 6px 10px;
  border-radius: 6px;
  margin-top: 6px;
  border-left: 3px solid #fbc4c4;
}

/* 加载状态 */
.loading-state {
  margin-top: 24px;
  padding: 32px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e9ecef;
}

/* 空状态 */
.weather-empty {
  padding: 48px 0;
  text-align: center;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .weather-content {
    padding: 20px;
  }
  
  .city-select-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .select-item {
    width: 100%;
  }
  
  .forecast-list {
    grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    gap: 12px;
  }
  
  .weather-overview h2 {
    font-size: 24px;
  }
  
  .weather-type {
    font-size: 36px;
  }
  
  .weather-temp {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .weather-container {
    padding: 10px;
  }
  
  .weather-content {
    padding: 16px;
  }
  
  .card-header {
    font-size: 16px;
    padding: 16px;
  }
  
  .forecast-list {
    grid-template-columns: 1fr;
  }
}
</style>