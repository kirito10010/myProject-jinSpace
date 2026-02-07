# 项目创建计划

## 后端（SpringBoot）

1. **创建项目结构**：使用Spring Initializr创建SpringBoot项目
2. **配置依赖**：添加web、MyBatisPlus、lombok、mysql、hutool等依赖
3. **配置数据库**：修改application.yml，配置mysql连接（账号root，密码123456，数据库名jinspace）
4. **创建基本目录结构**：controller、service、mapper、entity等
5. **添加启动类**：确保SpringBoot应用能正常启动

## 前端（Vue3）

1. **创建项目**：使用Vite创建Vue3 + TypeScript项目
2. **安装依赖**：安装Element Plus和Vue Router和Axios
3. **配置路由**：创建基本的路由配置
4. **配置Element Plus**：全局引入或按需引入组件
5. **创建基本页面结构**：首页、布局等

## 验证

1. 确保后端能正常启动
2. 确保前端能正常构建

项目将创建在当前目录下，后端和前端分别在不同的子目录中。
