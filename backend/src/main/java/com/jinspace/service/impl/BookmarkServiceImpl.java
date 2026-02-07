package com.jinspace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinspace.entity.Bookmark;
import com.jinspace.mapper.BookmarkMapper;
import com.jinspace.service.BookmarkService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户网址书签服务实现类
 */
@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {
    @Override
    public BookmarkMapper getBaseMapper() {
        return super.getBaseMapper();
    }
    
    @Override
    public Page<Bookmark> getBookmarksByUid(Long uid, int page, int pageSize) {
        // 创建分页对象
        Page<Bookmark> bookmarkPage = new Page<>(page, pageSize);
        
        // 创建查询条件
        QueryWrapper<Bookmark> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid)
                    .orderByDesc("created_time");
        
        // 执行分页查询
        return baseMapper.selectPage(bookmarkPage, queryWrapper);
    }
    
    @Override
    public boolean addBookmark(Bookmark bookmark) {
        // 保存书签信息
        return save(bookmark);
    }
    
    @Override
    public boolean updateBookmark(Bookmark bookmark) {
        // 更新书签信息
        return updateById(bookmark);
    }
    
    @Override
    public boolean deleteBookmarkById(Long id) {
        // 根据ID删除书签
        return removeById(id);
    }
    
    @Override
    public boolean batchDeleteBookmarks(List<Long> ids) {
        // 批量删除书签
        return removeByIds(ids);
    }
}
