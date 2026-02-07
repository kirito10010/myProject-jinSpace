package com.jinspace.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinspace.entity.Bookmark;
import com.jinspace.mapper.BookmarkMapper;

import java.util.List;

/**
 * 用户网址书签服务接口
 */
public interface BookmarkService extends IService<Bookmark> {
    /**
     * 获取基础Mapper
     * @return BookmarkMapper
     */
    BookmarkMapper getBaseMapper();
    /**
     * 获取用户的网址列表
     * @param uid 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Bookmark> getBookmarksByUid(Long uid, int page, int pageSize);
    
    /**
     * 添加网址书签
     * @param bookmark 书签信息
     * @return 是否添加成功
     */
    boolean addBookmark(Bookmark bookmark);
    
    /**
     * 修改网址书签
     * @param bookmark 书签信息
     * @return 是否修改成功
     */
    boolean updateBookmark(Bookmark bookmark);
    
    /**
     * 根据ID删除网址书签
     * @param id 书签ID
     * @return 是否删除成功
     */
    boolean deleteBookmarkById(Long id);
    
    /**
     * 批量删除网址书签
     * @param ids 书签ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteBookmarks(List<Long> ids);
}
