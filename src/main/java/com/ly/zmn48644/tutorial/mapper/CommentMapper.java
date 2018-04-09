package com.ly.zmn48644.tutorial.mapper;

import com.ly.zmn48644.tutorial.model.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    List<Comment> selectComment( Comment comment);
}
