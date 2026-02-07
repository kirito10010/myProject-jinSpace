package com.jinspace.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.Feedback;
import com.jinspace.mapper.FeedbackMapper;
import com.jinspace.service.FeedbackService;
import org.springframework.stereotype.Service;

/**
 * 反馈服务实现类
 * 实现反馈相关业务逻辑
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {
    @Override
    public FeedbackMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    @Override
    public boolean submitFeedback(Feedback feedback) {
        // 设置默认状态：1-待处理
        if (feedback.getStatus() == null) {
            feedback.setStatus(1);
        }
        // 保存反馈
        return save(feedback);
    }
}
