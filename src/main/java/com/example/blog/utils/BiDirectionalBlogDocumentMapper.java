package com.example.blog.utils;

import com.example.blog.Blog;
import com.example.blog.constants.Constants;
import org.bson.Document;

public class BiDirectionalBlogDocumentMapper {
    public static Blog mapDocumentToBlog(Document document){
        return Blog.newBuilder()
                .setId((String) document.get(Constants.ID))
                .setTitle((String) document.get(Constants.TITLE))
                .setAuthor((String) document.get(Constants.AUTHOR))
                .setContent((String) document.get(Constants.CONTENT))
                .build();
    }

    public static Document mapBlogToDocument(Blog blog){
        Document document = new Document();
        document.append(Constants.ID, blog.getId())
                .append(Constants.AUTHOR, blog.getAuthor())
                .append(Constants.TITLE, blog.getTitle())
                .append(Constants.CONTENT, blog.getContent());
        return document;
    }
}
