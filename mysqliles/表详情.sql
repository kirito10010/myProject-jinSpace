-- 用户表，用来登录注册的
CREATE TABLE `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID，主键',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号（唯一）',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码（如MD5/SHA256）',
  `nickname` VARCHAR(50) NOT NULL DEFAULT '用户' COMMENT '用户昵称',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱（唯一，用于找回密码）',
  `role` TINYINT UNSIGNED NOT NULL DEFAULT 3 COMMENT '用户权限：1-超级管理员，2-管理员，3-普通成员',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录注册表（带权限）';



-- 用户网址收藏表
CREATE TABLE user_bookmarks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    uid BIGINT NOT NULL COMMENT '用户ID',
    url VARCHAR(1000) NOT NULL COMMENT '网址',
    title VARCHAR(200) COMMENT '标题',
    remark VARCHAR(500) COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    -- 创建索引
    INDEX idx_uid (uid),
    INDEX idx_created (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户网址表';



-- 交易记录表
CREATE TABLE transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL COMMENT '金额，正数为收入，负数为支出',
    type ENUM('income', 'expense') NOT NULL COMMENT '收入或支出',
    category VARCHAR(50) NOT NULL COMMENT '分类（如：餐饮、工资）',
    description VARCHAR(200) COMMENT '描述',
    transaction_date DATE NOT NULL COMMENT '交易日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 创建索引
    INDEX idx_user_date (user_id, transaction_date DESC),
    INDEX idx_user_category (user_id, category),
    INDEX idx_user_type (user_id, type)
) COMMENT='交易记录表';



-- 安装文件表
CREATE TABLE installer_files (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '上传用户ID',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_size BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_hash VARCHAR(64) COMMENT '文件哈希（用于去重）',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    status ENUM('uploaded', 'verified', 'active', 'archived') DEFAULT 'uploaded' COMMENT '文件状态',
    version VARCHAR(50) COMMENT '程序版本号',
    platform ENUM('windows', 'linux', 'macos', 'android', 'ios') COMMENT '目标平台',
    description TEXT COMMENT '文件描述'
);
-- 创建索引
CREATE INDEX idx_user_id ON installer_files(user_id);
CREATE INDEX idx_upload_time ON installer_files(upload_time);
CREATE INDEX idx_status ON installer_files(status);
CREATE INDEX idx_file_hash ON installer_files(file_hash);



CREATE TABLE daily_performance (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '绩效记录ID',
    uid INT NOT NULL COMMENT '用户ID',
    date DATE NOT NULL COMMENT '绩效日期，格式：2026-01-24',
    project VARCHAR(100) NOT NULL COMMENT '生产项目',
    process VARCHAR(50) NOT NULL COMMENT '工序',
    quota_efficiency INT NOT NULL COMMENT '额定效率',
    actual_workload INT NOT NULL COMMENT '实际工作量',
    performance_man_days DECIMAL(10,5) NOT NULL COMMENT '绩效人天合计',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 添加索引以提高查询性能
    INDEX idx_uid (uid),
    INDEX idx_date (date),
    INDEX idx_project (project),
    INDEX idx_process (process),
    INDEX idx_uid_date (uid, date),
    INDEX idx_project_process (project, process),
    -- 检查约束（如果MySQL版本支持）
    CONSTRAINT chk_process CHECK (process IN ('作业', '质检'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日绩效记录表';


-- 生产项目枚举表
CREATE TABLE production_projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    project_code VARCHAR(50) UNIQUE NOT NULL,
    project_name VARCHAR(100) NOT NULL,
    uid INT NOT NULL COMMENT '项目负责人/创建人ID',
    work_quota_efficiency INT DEFAULT 0 COMMENT '作业工序额定效率',
    inspect_quota_efficiency INT DEFAULT 0 COMMENT '质检工序额定效率',
    status TINYINT DEFAULT 1 COMMENT '1:启用, 0:停用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_uid (uid),
    INDEX idx_status (status)
) COMMENT='生产项目参考表';



-- 基础薪资表结构
CREATE TABLE `salary_records` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `uid` VARCHAR(50) NOT NULL COMMENT '员工ID',
  `year_month` VARCHAR(7) NOT NULL COMMENT '年月，如2024-01',
  `period_start` DATE NOT NULL COMMENT '周期开始日期',
  `period_end` DATE NOT NULL COMMENT '周期结束日期',
  
  -- 前端传入的字段
  `attendance_days` INT NOT NULL COMMENT '出勤天数',
  `base_salary` DECIMAL(10,5) NOT NULL COMMENT '基本薪资',
  
  -- 绩效相关字段
  `performance` DECIMAL(10,5) NOT NULL COMMENT '绩效系数/评分',
  `performance_salary` DECIMAL(10,5) NOT NULL COMMENT '绩效薪资',
  `position_performance` DECIMAL(10,5) NOT NULL COMMENT '岗位绩效',
  
  `meal_allowance` DECIMAL(10,5) NOT NULL COMMENT '餐补',
  `housing_allowance` DECIMAL(10,5) NOT NULL COMMENT '房补',
  `full_attendance` DECIMAL(10,5) NOT NULL COMMENT '全勤奖',
  `other_bonus` DECIMAL(10,5) NOT NULL COMMENT '其他奖金',
  
  -- 社保扣除项
  `pension` DECIMAL(10,5) NOT NULL COMMENT '养老保险',
  `medical` DECIMAL(10,5) NOT NULL COMMENT '医疗保险',
  `unemployment` DECIMAL(10,5) NOT NULL COMMENT '失业保险',
  
  -- 迟到扣款
  `late_deduction` DECIMAL(10,5) DEFAULT 0.00000 COMMENT '迟到扣款',
  
  -- 加班相关字段
  `overtime_hours` DECIMAL(10,5) DEFAULT 0.00000 COMMENT '加班时长(小时)',
  `overtime_salary` DECIMAL(10,5) DEFAULT 0.00000 COMMENT '加班薪资',
  
  -- 总额字段
  `total_income` DECIMAL(10,5) NOT NULL COMMENT '应发总额',
  `total_deduction` DECIMAL(10,5) NOT NULL COMMENT '扣除总额',
  `net_salary` DECIMAL(10,5) NOT NULL COMMENT '实发薪资',
  
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 确保每个员工每月只有一条记录
  UNIQUE KEY `idx_uid_month` (`uid`, `year_month`),
  INDEX `idx_month` (`year_month`),
  INDEX `idx_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度薪资记录表';



-- 用户反馈表
CREATE TABLE feedback (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
    uid INT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '反馈内容',
    contact VARCHAR(100) COMMENT '联系方式',
    feedback_type TINYINT DEFAULT 1 COMMENT '反馈类型：1-建议，2-问题，3-投诉，4-其他',
    status TINYINT DEFAULT 1 COMMENT '状态：1-待处理，2-处理中，3-已处理，4-已关闭',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_uid (uid),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';



-- 创建加班小时汇报表
CREATE TABLE overtime_report (
    id INT PRIMARY KEY AUTO_INCREMENT,           -- 主键ID
    uid VARCHAR(50) NOT NULL,                   -- 用户ID
    report_date DATE NOT NULL,                  -- 汇报日期
    overtime_hours DECIMAL(3,1) NOT NULL,       -- 加班小时数（支持0.5的倍数）
    project_name VARCHAR(100),                  -- 项目名称（可选）
    description TEXT,                           -- 加班描述
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'approved', -- 状态
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 更新时间
    
    INDEX idx_uid (uid),
    INDEX idx_date (report_date),
    INDEX idx_status (status),
    UNIQUE KEY unique_report (uid, report_date) -- 防止同一天重复汇报
);
