package com.ly.zmn48644.tutorial.model;

import java.util.Date;
import java.util.List;

public class Post {
    private Integer id;
    private String subject;

    private Author author;
    private List<Comment> comments;
    private List<Tag> tags;



    private Integer blogId;

    private Date createdOn;
    private String section;
    private String draft;
    private String body;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", author=" + author +
                ", comments=" + comments +
                ", tags=" + tags +
                ", blogId=" + blogId +
                ", createdOn=" + createdOn +
                ", section='" + section + '\'' +
                ", draft='" + draft + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Integer getBlogId() {
        return blogId;
    }

    public void setBlogId(Integer blogId) {
        this.blogId = blogId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
