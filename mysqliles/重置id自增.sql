-- 将表里的id列，取消自增，取消主键
ALTER TABLE user_bookmarks MODIFY id INT(11) NOT NULL FIRST,DROP PRIMARY KEY;
  
-- 新增id2列，自增，主键。名字可以自定义。
ALTER TABLE user_bookmarks ADD id2 INT(11) NOT NULL AUTO_INCREMENT FIRST,ADD PRIMARY KEY (id2);
  
-- 删除id列
ALTER TABLE user_bookmarks DROP id;
  
-- 把id2改为id
ALTER TABLE user_bookmarks CHANGE id2 id INT(11) NOT NULL AUTO_INCREMENT FIRST;