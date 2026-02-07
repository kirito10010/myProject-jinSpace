# JinSpace 项目开发文档

## 1. 项目概述

JinSpace是一个综合性的Web应用平台，提供了书签管理、安装程序管理、财务管理、绩效管理、资源管理和实用工具等多种功能模块。该项目采用前后端分离架构，前端基于Vue 3 + TypeScript开发，后端基于Spring Boot + MyBatis Plus开发，数据库使用MySQL。

### 1.1 项目目标

- 提供便捷的书签管理功能，支持用户收藏和管理网址链接
- 实现安装程序的上传、下载和管理功能
- 实现资源压缩包的上传、下载和管理功能
- 提供财务台账功能，支持用户记账、查看收支情况
- 实现绩效管理功能，支持用户查看绩效数据
- 提供实用工具，如音乐搜索和天气查询
- 提供管理员功能，支持用户管理和反馈管理
- 提供用户认证和授权功能，确保数据安全

### 1.2 项目特点

- 前后端分离架构，便于团队协作开发
- 采用现代化技术栈，支持快速开发和扩展
- 响应式设计，适配不同设备屏幕
- 模块化设计，便于功能扩展和维护
- 良好的用户体验，界面简洁美观

## 2. 技术栈

### 2.1 后端技术栈

| 技术/框架 | 版本 | 用途 |
| --- | --- | --- |
| Spring Boot | 2.7.18 | 后端开发框架 |
| MyBatis Plus | 3.5.3.2 | ORM框架，简化数据库操作 |
| MySQL | 8.0.33 | 关系型数据库 |
| Maven | 3.x | 项目构建工具 |
| JDK | 1.8 | Java开发环境 |

### 2.2 前端技术栈

| 技术/框架 | 版本 | 用途 |
| --- | --- | --- |
| Vue | 3.5.24 | 前端框架 |
| TypeScript | 5.9.3 | 类型安全的JavaScript超集 |
| Element Plus | 2.13.1 | UI组件库 |
| Vite | 7.3.1 | 前端构建工具 |
| Vue Router | 4.6.4 | 前端路由管理 |
| Axios | 1.13.2 | HTTP客户端，用于前后端通信 |
| ECharts | 6.0.0 | 数据可视化图表库 |
| xlsx | 0.18.5 | Excel文件处理库 |

### 2.3 部署技术栈

| 技术/工具 | 版本 | 用途 |
| --- | --- | --- |
| NGINX | 1.28.1 | Web服务器，用于前端静态资源部署和反向代理 |

## 3. 目录结构

### 3.1 项目根目录结构

```
JinSpace/
├── backend/               # 后端Spring Boot项目
├── frontend/              # 前端Vue 3项目
├── nginx-1.28.1/          # NGINX服务器
├── DEVELOPMENT.md         # 开发文档
└── .trae/                 # 项目规划文档
```

### 3.2 后端目录结构

```
backend/
├── src/
│   └── main/
│       ├── java/com/jinspace/
│       │   ├── common/         # 公共类
│       │   │   └── Result.java        # 统一返回结果类
│       │   ├── config/         # 配置类
│       │   │   ├── CorsConfig.java        # CORS配置
│       │   │   └── MyMetaObjectHandler.java # MyBatis Plus元对象处理器
│       │   ├── controller/      # 控制器层
│       │   │   ├── AuthController.java        # 认证控制器
│       │   │   ├── BookmarkController.java    # 书签控制器
│       │   │   ├── CityController.java        # 城市控制器
│       │   │   ├── DailyPerformanceController.java # 日常绩效控制器
│       │   │   ├── FeedbackController.java    # 反馈控制器
│       │   │   ├── InstallerFileController.java # 安装程序控制器
│       │   │   ├── ProductionProjectController.java # 生产项目控制器
│       │   │   ├── SalaryRecordController.java # 薪资记录控制器
│       │   │   ├── TransactionController.java  # 交易控制器
│       │   │   ├── UserController.java        # 用户控制器
│       │   │   └── WeatherController.java     # 天气控制器
│       │   ├── entity/          # 实体类
│       │   │   ├── Bookmark.java        # 书签实体
│       │   │   ├── City.java            # 城市实体
│       │   │   ├── DailyPerformance.java # 日常绩效实体
│       │   │   ├── Feedback.java         # 反馈实体
│       │   │   ├── InstallerFile.java    # 安装程序实体
│       │   │   ├── ProductionProject.java # 生产项目实体
│       │   │   ├── SalaryRecord.java     # 薪资记录实体
│       │   │   ├── Transaction.java      # 交易实体
│       │   │   └── User.java            # 用户实体
│       │   ├── mapper/          # 数据访问层
│       │   │   ├── BookmarkMapper.java        # 书签映射
│       │   │   ├── CityMapper.java            # 城市映射
│       │   │   ├── DailyPerformanceMapper.java # 日常绩效映射
│       │   │   ├── FeedbackMapper.java        # 反馈映射
│       │   │   ├── InstallerFileMapper.java    # 安装程序映射
│       │   │   ├── ProductionProjectMapper.java # 生产项目映射
│       │   │   ├── SalaryRecordMapper.java     # 薪资记录映射
│       │   │   ├── TransactionMapper.java      # 交易映射
│       │   │   └── UserMapper.java            # 用户映射
│       │   ├── service/         # 业务逻辑层
│       │   │   ├── impl/         # 服务实现
│       │   │   │   ├── BookmarkServiceImpl.java        # 书签服务实现
│       │   │   │   ├── CityServiceImpl.java            # 城市服务实现
│       │   │   │   ├── DailyPerformanceServiceImpl.java # 日常绩效服务实现
│       │   │   │   ├── FeedbackServiceImpl.java        # 反馈服务实现
│       │   │   │   ├── InstallerFileServiceImpl.java    # 安装程序服务实现
│       │   │   │   ├── ProductionProjectServiceImpl.java # 生产项目服务实现
│       │   │   │   ├── SalaryRecordServiceImpl.java     # 薪资记录服务实现
│       │   │   │   ├── TransactionServiceImpl.java      # 交易服务实现
│       │   │   │   ├── UserServiceImpl.java            # 用户服务实现
│       │   │   │   └── WeatherServiceImpl.java         # 天气服务实现
│       │   │   ├── BookmarkService.java        # 书签服务接口
│       │   │   ├── CityService.java            # 城市服务接口
│       │   │   ├── DailyPerformanceService.java # 日常绩效服务接口
│       │   │   ├── FeedbackService.java        # 反馈服务接口
│       │   │   ├── InstallerFileService.java    # 安装程序服务接口
│       │   │   ├── ProductionProjectService.java # 生产项目服务接口
│       │   │   ├── SalaryRecordService.java     # 薪资记录服务接口
│       │   │   ├── TransactionService.java      # 交易服务接口
│       │   │   ├── UserService.java            # 用户服务接口
│       │   │   └── WeatherService.java         # 天气服务接口
│       │   └── BackendApplication.java  # 应用入口
│       └── resources/
│           ├── mapper/          # MyBatis映射文件
│           │   └── UserMapper.xml        # 用户映射文件
│           └── application.yml  # 应用配置文件
├── target/                  # 编译输出目录
└── pom.xml                 # Maven配置文件
```

### 3.3 前端目录结构

```
frontend/
├── public/              # 静态资源目录
├── src/
│   ├── api/             # API请求封装
│   │   ├── auth.ts              # 认证相关API
│   │   ├── bookmarks.ts         # 书签相关API
│   │   ├── feedback.ts          # 反馈相关API
│   │   ├── music.ts             # 音乐相关API
│   │   ├── performance.ts       # 绩效相关API
│   │   ├── projects.ts          # 生产项目相关API
│   │   ├── request.ts           # 请求基础配置
│   │   ├── salary.ts            # 薪资相关API
│   │   ├── transactions.ts      # 交易相关API
│   │   └── users.ts             # 用户相关API
│   ├── assets/          # 前端资源文件
│   │   ├── login-background/    # 登录背景图片
│   │   └── vue.svg              # Vue图标
│   ├── router/          # 路由配置
│   │   └── index.ts             # 路由定义
│   ├── utils/           # 工具类
│   │   └── errorFilter.ts       # 错误过滤工具
│   ├── views/           # 页面组件
│   │   ├── AdminFeedback.vue        # 管理员反馈页面
│   │   ├── AdminUserManagement.vue  # 管理员用户管理页面
│   │   ├── DataFinance.vue          # 财务管理页面
│   │   ├── DataPerformance.vue      # 绩效管理页面
│   │   ├── DataStatistics.vue       # 绩效记录与统计页面
│   │   ├── HomeView.vue             # 主页
│   │   ├── LoginView.vue            # 登录页面
│   │   ├── RegisterView.vue         # 注册页面
│   │   ├── ResourceExe.vue          # 安装程序管理页面
│   │   ├── ResourceUrl.vue          # 书签管理页面
│   │   ├── ResourceZip.vue          # 资源压缩包管理页面
│   │   ├── ToolsMusic.vue           # 音乐工具页面
│   │   └── ToolsWeather.vue         # 天气工具页面
│   ├── App.vue          # 根组件
│   ├── main.ts          # 应用入口
│   └── style.css        # 全局样式
├── dist/                # 构建输出目录
├── index.html           # HTML模板
├── package.json         # 项目依赖配置
├── tsconfig.json        # TypeScript配置
└── vite.config.ts       # Vite配置
```

## 4. 功能模块

### 4.1 用户认证模块

- **登录功能**：支持用户名和密码登录
- **注册功能**：支持新用户注册
- **身份验证**：使用JWT令牌进行身份验证
- **权限控制**：基于用户角色的权限控制

### 4.2 书签管理模块

- **书签列表**：展示用户收藏的书签
- **添加书签**：支持添加新的书签
- **编辑书签**：支持编辑现有书签
- **删除书签**：支持删除书签
- **批量删除**：支持批量删除书签
- **分页功能**：支持分页显示书签

### 4.3 安装程序管理模块

- **文件上传**：支持上传安装程序文件
- **文件列表**：展示用户上传的文件
- **文件下载**：支持下载安装程序文件
- **文件管理**：支持查看和管理文件信息
- **编辑文件**：支持编辑文件信息
- **删除文件**：支持删除文件
- **批量删除**：支持批量删除文件
- **分页功能**：支持分页显示文件

### 4.4 资源压缩包管理模块

- **文件上传**：支持上传资源压缩包文件
- **文件列表**：展示用户上传的压缩包文件
- **文件下载**：支持下载资源压缩包文件
- **文件管理**：支持查看和管理压缩包文件信息

### 4.5 财务管理模块

- **记账功能**：支持添加收入和支出记录
- **财务报表**：展示收入和支出趋势
- **交易管理**：支持查看、编辑和删除交易记录
- **收支分析**：使用ECharts展示收支情况

### 4.6 绩效管理模块

- **绩效数据展示**：展示用户的绩效数据
- **绩效分析**：支持绩效数据的分析和统计
- **数据可视化**：使用ECharts展示绩效数据
- **Excel导入**：支持从Excel文件导入绩效数据

### 4.7 日常绩效记录模块

- **绩效记录列表**：展示用户的日常绩效记录
- **添加绩效记录**：支持添加每日绩效记录
- **编辑绩效记录**：支持编辑现有绩效记录
- **删除绩效记录**：支持删除绩效记录
- **批量删除**：支持批量删除绩效记录
- **绩效统计**：统计本周、本月或自定义时间范围的绩效总和，自动扣除加班人天
- **绩效趋势图**：使用ECharts展示绩效趋势

### 4.8 生产项目配置模块

- **生产项目列表**：展示用户配置的生产项目
- **添加生产项目**：支持添加新的生产项目
- **编辑生产项目**：支持编辑现有生产项目
- **删除生产项目**：支持删除生产项目
- **批量删除**：支持批量删除生产项目
- **项目编码唯一性**：确保每个项目编码唯一

### 4.9 加班汇报模块

- **加班记录列表**：展示用户的加班记录
- **添加加班记录**：支持添加每日加班记录
- **编辑加班记录**：支持编辑现有加班记录
- **删除加班记录**：支持删除加班记录
- **批量删除**：支持批量删除加班记录
- **加班状态**：默认状态为已通过
- **项目关联**：支持关联生产项目
- **描述字段**：支持添加加班描述
- **加班时长**：支持0.5小时的倍数

### 4.10 薪资记录管理模块

- **薪资记录列表**：展示用户的薪资记录
- **添加薪资记录**：支持添加工资记录
- **编辑薪资记录**：支持编辑现有薪资记录
- **删除薪资记录**：支持删除薪资记录
- **批量删除**：支持批量删除薪资记录
- **自动计算**：自动计算绩效薪资、加班薪资等
- **迟到影响**：根据迟到分钟数影响基本薪资、岗位绩效、餐补和房补
- **默认配置**：支持用户自定义薪资记录默认值
- **时间周期**：自动计算周期开始和结束日期（26日至下月25日）
- **自动填充**：自动填充考勤天数、绩效系数和加班时长
- **绩效系数**：根据日常绩效记录自动计算绩效系数
- **加班时长**：根据加班记录自动计算加班时长总和

### 4.11 反馈管理模块

- **反馈提交**：支持用户提交反馈
- **反馈列表**：展示用户反馈信息
- **反馈处理**：支持处理和回复用户反馈
- **编辑反馈**：支持编辑反馈信息
- **删除反馈**：支持删除反馈
- **批量删除**：支持批量删除反馈
- **分页功能**：支持分页显示反馈

### 4.12 实用工具模块

#### 4.12.1 音乐工具
- **音乐搜索**：支持根据关键词搜索音乐
- **音乐播放**：支持在线播放音乐

#### 4.12.2 天气工具
- **城市查询**：支持查询城市天气信息
- **天气展示**：展示城市的天气状况、温度、湿度等信息

### 4.13 用户反馈模块

#### 4.13.1 用户反馈功能
- **反馈提交**：用户可通过下拉菜单中的反馈选项，打开反馈弹窗提交问题
- **反馈表单**：包含反馈类型选择、反馈内容输入等字段

#### 4.13.2 管理员反馈管理
- **反馈列表**：展示所有用户反馈信息，包含用户昵称、反馈类型、内容、时间等
- **反馈处理**：支持查看、编辑、删除单个反馈
- **批量操作**：支持批量删除反馈
- **分页功能**：默认每页显示10条反馈记录
- **用户信息展示**：根据用户ID关联显示用户昵称

### 4.14 管理员模块

#### 4.14.1 用户管理
- **用户列表**：展示系统所有用户
- **用户管理**：支持查看和管理用户信息

## 5. 数据库设计

### 5.1 数据库表结构

#### 5.1.1 用户表 (users)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 用户ID | PRIMARY KEY, AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | UNIQUE, NOT NULL |
| password | VARCHAR(100) | 密码 | NOT NULL |
| email | VARCHAR(100) | 邮箱 | UNIQUE, NOT NULL |
| nickname | VARCHAR(50) | 昵称 | |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

#### 5.1.2 书签表 (bookmarks)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 书签ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | INT | 用户ID | NOT NULL, FOREIGN KEY |
| url | VARCHAR(500) | 书签URL | NOT NULL |
| title | VARCHAR(255) | 书签标题 | NOT NULL |
| description | TEXT | 书签描述 | |
| category | VARCHAR(50) | 书签分类 | |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

#### 5.1.3 安装程序表 (installer_files)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 文件ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | INT | 上传用户ID | NOT NULL, FOREIGN KEY |
| file_name | VARCHAR(255) | 原始文件名 | NOT NULL |
| file_path | VARCHAR(500) | 存储路径 | NOT NULL |
| file_size | BIGINT | 文件大小（字节） | NOT NULL |
| file_hash | VARCHAR(64) | 文件哈希（用于去重） | |
| upload_time | DATETIME | 上传时间 | DEFAULT CURRENT_TIMESTAMP |
| status | ENUM | 文件状态 | DEFAULT 'uploaded' |
| version | VARCHAR(50) | 程序版本号 | |
| platform | ENUM | 目标平台 | |
| description | TEXT | 文件描述 | |

#### 5.1.4 交易表 (transactions)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 交易ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | INT | 用户ID | NOT NULL, FOREIGN KEY |
| amount | DECIMAL(10,2) | 金额 | NOT NULL |
| type | ENUM | 交易类型 | NOT NULL |
| category | VARCHAR(50) | 分类 | NOT NULL |
| description | TEXT | 描述 | |
| transaction_date | DATE | 交易日期 | NOT NULL |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

#### 5.1.5 城市表 (cities)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 城市ID | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | 城市名称 | NOT NULL |
| code | VARCHAR(50) | 城市代码 | NOT NULL |
| created_at | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

#### 5.1.6 日常绩效记录表 (daily_performance)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 绩效记录ID | PRIMARY KEY, AUTO_INCREMENT |
| uid | INT | 用户ID | NOT NULL |
| date | DATE | 绩效日期 | NOT NULL |
| project | VARCHAR(100) | 生产项目 | NOT NULL |
| process | VARCHAR(50) | 工序 | NOT NULL, CHECK (process IN ('作业', '质检')) |
| quota_efficiency | INT | 额定效率 | NOT NULL |
| actual_workload | INT | 实际工作量 | NOT NULL |
| performance_man_days | DECIMAL(10,5) | 绩效人天合计 | NOT NULL |
| created_at | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| INDEX idx_uid (uid) | | 用户ID索引 | |
| INDEX idx_date (date) | | 日期索引 | |
| INDEX idx_project (project) | | 项目索引 | |
| INDEX idx_process (process) | | 工序索引 | |
| INDEX idx_uid_date (uid, date) | | 用户-日期联合索引 | |
| INDEX idx_project_process (project, process) | | 项目-工序联合索引 | |

#### 5.1.7 生产项目表 (production_projects)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 项目ID | PRIMARY KEY, AUTO_INCREMENT |
| project_code | VARCHAR(50) | 项目编码 | UNIQUE, NOT NULL |
| project_name | VARCHAR(100) | 项目名称 | NOT NULL |
| uid | INT | 用户ID | NOT NULL |
| work_quota_efficiency | INT | 作业额定效率 | NOT NULL |
| inspect_quota_efficiency | INT | 质检额定效率 | NOT NULL |
| status | INT | 状态 | DEFAULT 1 |
| created_at | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| INDEX idx_uid (uid) | | 用户ID索引 | |
| INDEX idx_project_code (project_code) | | 项目编码索引 | |

#### 5.1.8 月度薪资记录表 (salary_records)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 薪资记录ID | PRIMARY KEY, AUTO_INCREMENT |
| uid | VARCHAR(50) | 员工ID | NOT NULL |
| year_month | VARCHAR(7) | 年月，如2024-01 | NOT NULL |
| period_start | DATE | 周期开始日期 | NOT NULL |
| period_end | DATE | 周期结束日期 | NOT NULL |
| attendance_days | INT | 出勤天数 | NOT NULL |
| base_salary | DECIMAL(10,5) | 基本薪资 | NOT NULL |
| performance | DECIMAL(10,5) | 绩效系数/评分 | NOT NULL |
| performance_salary | DECIMAL(10,5) | 绩效薪资 | NOT NULL |
| position_performance | DECIMAL(10,5) | 岗位绩效 | NOT NULL |
| meal_allowance | DECIMAL(10,5) | 餐补 | NOT NULL |
| housing_allowance | DECIMAL(10,5) | 房补 | NOT NULL |
| full_attendance | DECIMAL(10,5) | 全勤奖 | NOT NULL |
| other_bonus | DECIMAL(10,5) | 其他奖金 | NOT NULL |
| pension | DECIMAL(10,5) | 养老保险 | NOT NULL |
| medical | DECIMAL(10,5) | 医疗保险 | NOT NULL |
| unemployment | DECIMAL(10,5) | 失业保险 | NOT NULL |
| late_deduction | DECIMAL(10,5) | 迟到分钟 | DEFAULT 0.00000 |
| overtime_hours | DECIMAL(10,5) | 加班时长(小时) | DEFAULT 0.00000 |
| overtime_salary | DECIMAL(10,5) | 加班薪资 | DEFAULT 0.00000 |
| total_income | DECIMAL(10,5) | 应发总额 | NOT NULL |
| total_deduction | DECIMAL(10,5) | 扣除总额 | NOT NULL |
| net_salary | DECIMAL(10,5) | 实发薪资 | NOT NULL |
| created_at | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| UNIQUE KEY idx_uid_month (uid, year_month) | | 用户-年月联合唯一索引 | |
| INDEX idx_month (year_month) | | 年月索引 | |
| INDEX idx_uid (uid) | | 用户ID索引 | |

#### 5.1.9 反馈表 (feedback)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | BIGINT | 反馈ID | PRIMARY KEY, AUTO_INCREMENT |
| uid | BIGINT | 用户ID | NOT NULL |
| content | VARCHAR(500) | 反馈内容 | NOT NULL |
| contact | VARCHAR(100) | 联系方式 | |
| feedback_type | INT | 反馈类型：1-建议，2-问题，3-投诉，4-其他 | NOT NULL |
| status | INT | 状态：1-待处理，2-处理中，3-已处理，4-已关闭 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| INDEX idx_uid (uid) | | 用户ID索引 | |
| INDEX idx_status (status) | | 状态索引 | |
| INDEX idx_feedback_type (feedback_type) | | 反馈类型索引 | |

#### 5.1.10 加班小时汇报表 (overtime_report)

| 字段名 | 数据类型 | 描述 | 约束 |
| --- | --- | --- | --- |
| id | INT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| uid | VARCHAR(50) | 用户ID | NOT NULL |
| report_date | DATE | 汇报日期 | NOT NULL |
| overtime_hours | DECIMAL(3,1) | 加班小时数（支持0.5的倍数） | NOT NULL |
| project_name | VARCHAR(100) | 项目名称（可选） | |
| description | TEXT | 加班描述 | |
| status | ENUM('pending', 'approved', 'rejected') | 状态，默认已通过 | DEFAULT 'approved' |
| created_at | TIMESTAMP | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| INDEX idx_uid (uid) | | 用户ID索引 | |
| INDEX idx_date (report_date) | | 日期索引 | |
| INDEX idx_status (status) | | 状态索引 | |
| UNIQUE KEY unique_report (uid, report_date) | | 防止同一天重复汇报 | |

## 6. API接口文档

### 6.1 认证接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/register` | POST | 用户注册 |

### 6.2 书签接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/bookmarks` | GET | 获取用户书签列表 |
| `/api/bookmarks` | POST | 添加新书签 |
| `/api/bookmarks/{id}` | PUT | 更新书签 |
| `/api/bookmarks/{id}` | DELETE | 删除书签 |
| `/api/bookmarks/batch` | DELETE | 批量删除书签 |

### 6.3 安装程序接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/installer-files/upload` | POST | 上传安装程序 |
| `/api/installer-files/user/{userId}` | GET | 获取用户文件列表 |
| `/api/installer-files` | PUT | 更新文件信息 |
| `/api/installer-files/download/{id}` | GET | 下载安装程序 |
| `/api/installer-files/{id}` | DELETE | 删除安装程序 |
| `/api/installer-files/batch` | DELETE | 批量删除安装程序 |

### 6.4 交易接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/transactions` | GET | 获取交易列表 |
| `/api/transactions` | POST | 添加交易记录 |
| `/api/transactions/{id}` | PUT | 更新交易记录 |
| `/api/transactions/{id}` | DELETE | 删除交易记录 |
| `/api/transactions/stats` | GET | 获取交易统计数据 |

### 6.5 用户接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/users` | GET | 获取用户列表 |
| `/api/users/{id}` | GET | 获取用户详情 |
| `/api/users/{id}` | PUT | 更新用户信息 |
| `/api/users/{id}` | DELETE | 删除用户 |

### 6.6 反馈接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/feedback` | POST | 提交反馈 |
| `/api/feedback` | GET | 获取反馈列表（管理员） |
| `/api/feedback/{id}` | PUT | 更新反馈（管理员） |
| `/api/feedback/{id}/status` | PUT | 更新反馈状态（管理员） |
| `/api/feedback/{id}` | DELETE | 删除反馈（管理员） |
| `/api/feedback/batch` | DELETE | 批量删除反馈（管理员）

### 6.7 城市接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/cities` | GET | 获取城市列表 |
| `/api/cities/{id}` | GET | 获取城市详情 |
| `/api/cities` | POST | 添加城市 |
| `/api/cities/{id}` | PUT | 更新城市信息 |
| `/api/cities/{id}` | DELETE | 删除城市 |

### 6.8 天气接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/weather/city/{cityCode}` | GET | 获取城市天气信息 |

### 6.9 日常绩效接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/performance/add` | POST | 添加绩效记录 |
| `/api/performance/update` | PUT | 更新绩效记录 |
| `/api/performance/list` | GET | 获取绩效记录列表 |
| `/api/performance/{id}` | DELETE | 删除绩效记录 |
| `/api/performance/batch` | DELETE | 批量删除绩效记录 |
| `/api/performance/range` | GET | 根据日期范围获取绩效记录 |

### 6.10 生产项目接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/project/add` | POST | 添加生产项目 |
| `/api/project/update` | PUT | 更新生产项目 |
| `/api/project/list` | GET | 获取生产项目列表 |
| `/api/project/{id}` | DELETE | 删除生产项目 |
| `/api/project/batch` | DELETE | 批量删除生产项目 |

### 6.11 薪资记录接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/salary/add` | POST | 添加工资记录 |
| `/api/salary/update` | PUT | 更新工资记录 |
| `/api/salary/list` | GET | 获取工资记录列表 |
| `/api/salary/{id}` | DELETE | 删除工资记录 |
| `/api/salary/batch` | DELETE | 批量删除工资记录 |

### 6.12 加班汇报接口

| URL | 方法 | 功能 |
| --- | --- | --- |
| `/api/overtime/add` | POST | 添加加班记录 |
| `/api/overtime/update` | PUT | 更新加班记录 |
| `/api/overtime/list` | GET | 获取加班记录列表 |
| `/api/overtime/listByDateRange` | GET | 根据日期范围获取加班记录列表 |
| `/api/overtime/{id}` | DELETE | 删除加班记录 |
| `/api/overtime/batch` | DELETE | 批量删除加班记录 |

## 7. 前端页面说明

### 7.1 登录页面 (LoginView.vue)

- **功能**：用户登录
- **主要组件**：表单输入框、登录按钮
- **路由**：`/login`

### 7.2 注册页面 (RegisterView.vue)

- **功能**：用户注册
- **主要组件**：表单输入框、注册按钮
- **路由**：`/register`

### 7.3 主页 (HomeView.vue)

- **功能**：系统主页，包含导航菜单
- **主要组件**：侧边栏导航、内容区域
- **路由**：`/home`

### 7.4 书签管理页面 (ResourceUrl.vue)

- **功能**：管理用户书签
- **主要组件**：书签列表、添加书签按钮、编辑按钮、删除按钮、批量删除按钮、分页组件
- **路由**：`/home/resource/url`

### 7.5 安装程序管理页面 (ResourceExe.vue)

- **功能**：上传和管理安装程序
- **主要组件**：文件上传按钮、文件列表、编辑按钮、删除按钮、批量删除按钮、下载按钮、分页组件
- **路由**：`/home/resource/exe`

### 7.6 资源压缩包管理页面 (ResourceZip.vue)

- **功能**：上传和管理资源压缩包
- **主要组件**：文件上传按钮、文件列表
- **路由**：`/home/resource/zip`

### 7.7 财务管理页面 (DataFinance.vue)

- **功能**：记账和查看财务数据
- **主要组件**：记账按钮、交易列表、收支图表
- **路由**：`/home/data/finance`

### 7.8 绩效管理页面 (DataPerformance.vue)

- **功能**：查看绩效数据
- **主要组件**：绩效图表、数据表格、Excel导入按钮
- **路由**：`/home/data/performance`

### 7.9 绩效记录与统计页面 (DataStatistics.vue)

- **功能**：日常绩效记录管理和统计分析
- **主要组件**：
  - 工作汇报按钮：打开绩效记录添加/编辑弹窗
  - 加班汇报按钮：打开加班记录管理弹窗
  - 工资记录按钮：打开薪资记录管理弹窗
  - 生产项目配置按钮：打开生产项目配置弹窗
  - 时间范围选择器：支持本周、本月、选择月份和自定义时间范围
  - 绩效总和展示：显示当月绩效总和、考勤天数和日均绩效，自动扣除加班人天
  - 绩效趋势图：使用ECharts展示绩效变化趋势
- **路由**：`/home/data/statistics`

### 7.10 加班汇报页面 (DataStatistics.vue - 弹窗)

- **功能**：加班记录的管理和统计
- **主要组件**：
  - 加班记录列表：展示用户的加班记录
  - 添加加班记录按钮：打开加班记录添加/编辑弹窗
  - 批量删除按钮：支持批量删除加班记录
  - 加班记录表单：包含汇报日期、加班小时数、项目名称和加班描述
  - 加班状态：默认状态为已通过
- **说明**：作为绩效记录与统计页面的弹窗组件使用

### 7.11 音乐工具页面 (ToolsMusic.vue)

- **功能**：搜索和播放音乐
- **主要组件**：搜索框、音乐列表、播放器
- **路由**：`/home/tools/music`

### 7.12 天气工具页面 (ToolsWeather.vue)

- **功能**：查询城市天气
- **主要组件**：城市选择器、天气信息展示
- **路由**：`/home/tools/weather`

### 7.13 管理员用户管理页面 (AdminUserManagement.vue)

- **功能**：管理系统用户
- **主要组件**：用户列表、用户管理按钮
- **路由**：`/home/admin/users`

### 7.14 管理员反馈管理页面 (AdminFeedback.vue)

- **功能**：管理用户反馈
- **主要组件**：反馈列表、反馈处理按钮、编辑按钮、删除按钮、批量删除按钮、分页组件
- **路由**：`/home/admin/feedback`

## 8. 部署说明

### 8.1 环境要求

- JDK 1.8+
- MySQL 8.0+
- Node.js 16+
- NGINX 1.20+

### 8.2 后端部署

1. 编译后端项目：
   ```bash
   cd backend
   mvn clean package
   ```

2. 运行后端服务：
   ```bash
   java -jar target/backend-1.0-SNAPSHOT.jar
   ```

### 8.3 前端部署

1. 安装依赖：
   ```bash
   cd frontend
   npm install
   ```

2. 构建前端项目：
   ```bash
   npm run build
   ```

3. 部署到NGINX：
   - 将`dist`目录下的文件复制到NGINX的`html`目录
   - 配置NGINX反向代理，指向后端服务

### 8.4 NGINX配置示例

```nginx
server {
    listen       80;
    server_name  localhost;

    location / {
        root   html;
        index  index.html index.htm;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8086;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 9. 开发指南

### 9.1 开发环境搭建

1. 克隆项目代码
2. 安装后端依赖：
   ```bash
   cd backend
   mvn install
   ```

3. 安装前端依赖：
   ```bash
   cd frontend
   npm install
   ```

### 9.2 数据库初始化

1. 创建MySQL数据库：`jinspace`
2. 执行数据库表创建语句（见5.1节）
3. 配置`application.yml`中的数据库连接信息

### 9.3 开发流程

1. 启动后端服务：
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. 启动前端开发服务器：
   ```bash
   cd frontend
   npm run dev
   ```

3. 访问前端应用：`http://localhost:5273`

### 9.4 代码规范

- 后端：遵循Java代码规范，使用Spring Boot最佳实践
- 前端：遵循Vue 3和TypeScript最佳实践
- 提交代码前执行格式化和检查

## 10. 常见问题

### 10.1 跨域问题

- 后端已配置CORS，允许所有来源访问
- 配置类：`com.jinspace.config.CorsConfig`

### 10.2 文件上传大小限制

- 后端默认支持1024MB的文件上传
- 配置项：`spring.servlet.multipart.max-file-size`和`spring.servlet.multipart.max-request-size`

### 10.3 数据库连接问题

- 检查`application.yml`中的数据库连接配置
- 确保MySQL服务正在运行
- 确保数据库用户具有正确的权限

### 10.4 前端开发服务器端口

- 前端开发服务器默认端口：5273
- 可在`vite.config.ts`中修改

## 11. 联系方式

- 项目负责人：XXX
- 技术支持：XXX
- 邮箱：XXX

## 12. 版本历史

| 版本 | 日期 | 描述 |
| --- | --- | --- |
| 1.0.0 | 2026-01-24 | 初始版本 |
| 1.1.0 | 2026-01-27 | 添加城市和天气功能，新增资源压缩包管理，添加管理员页面和工具页面 |
| 1.2.0 | 2026-01-31 | 新增日常绩效记录模块、生产项目配置模块和薪资记录管理模块，优化时间显示格式，修复SQL语法错误 |
| 1.3.0 | 2026-02-03 | 新增用户反馈功能模块，包括反馈提交和管理员反馈管理；增强书签管理模块，添加编辑、删除、批量删除和分页功能；调整ResourceExe模块结构与ResourceUrl保持一致；优化ResourceExe模块UI，调整按钮间距和布局；修复反馈UI问题，包括时间格式和用户昵称显示；更新项目文档，完善功能描述和API文档 |
| 1.4.0 | 2026-02-07 | 新增加班汇报模块，支持加班记录的添加、编辑、删除和批量删除；优化绩效统计，自动扣除加班人天；增强薪资记录管理，自动填充考勤天数、绩效系数和加班时长；优化加班记录状态，默认状态为已通过；添加加班记录API接口；更新数据库设计，添加加班小时汇报表；更新项目文档，完善功能描述和API文档 |

---

**文档更新日期**：2026-02-07
**文档作者**：AI Assistant