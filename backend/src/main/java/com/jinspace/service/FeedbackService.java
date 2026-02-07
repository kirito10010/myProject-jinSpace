package com.jinspace.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.Feedback;
import com.jinspace.mapper.FeedbackMapper;

/**
 * 反馈服务接口
 * 定义反馈相关业务方法
 */
public interface FeedbackService extends IService<Feedback> {
    /**
     * 获取基础Mapper
     * @return FeedbackMapper
     */
    FeedbackMapper getBaseMapper();
    
    /**
     * 提交反馈
     * @param feedback 反馈信息
     * @return 提交结果（true成功，false失败）
     */
    boolean submitFeedback(Feedback feedback);
}
