package services;

import com.example.blog.*;
import com.example.blog.impl.BlogService;
import com.google.protobuf.Empty;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;
import setup.MongoDbTestSetup;

import java.util.Collections;
import java.util.Objects;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Slf4j
public class BlogServiceTest extends MongoDbTestSetup {
    private BlogServiceGrpc.BlogServiceBlockingStub blockingStub;

    @BeforeAll
    public void init(){
        blockingStub = BlogServiceGrpc.newBlockingStub(managedChannel);
    }
    @Test
    public void test_create_blog(){
        BlogId blogId = blockingStub.createBlog(Blog.newBuilder()
                .setAuthor("mehra")
                .setTitle("first blog")
                .setContent("This is first blog of Ashish Nehra!!")
                .build());
        log.info("[Test]: test_create_blog, blogId: {}", blogId.getId());
    }

    @Test
    public void test_read_blog(){
        BlogList blogList = blockingStub.fetchAllBlogs(Empty.newBuilder().build());
        String firstBlogId = blogList.getBlogs(0).getId();
        Blog blog = blockingStub.readBlog(BlogId.newBuilder().setId(firstBlogId).build());
        assertEquals(firstBlogId, blog.getId());
    }

    @Test
    public void test_update_blog(){
        BlogList blogList = blockingStub.fetchAllBlogs(Empty.newBuilder().build());
        String firstBlogId = blogList.getBlogs(0).getId();
        UpdateRequest updateRequest = UpdateRequest.newBuilder().setId(firstBlogId).setContent("this is updated content").build();
        Blog blog = blockingStub.updateBlog(updateRequest);
        assertEquals("this is updated content", blog.getContent());
    }

    @Test
    public void test_delete_blog(){
        BlogList blogList = blockingStub.fetchAllBlogs(Empty.newBuilder().build());
        String firstBlogId = blogList.getBlogs(0).getId();
        if(Objects.nonNull(firstBlogId)){
             blockingStub.deleteBlog(BlogId.newBuilder().setId(firstBlogId).build());
             Blog blog = blockingStub.readBlog(BlogId.newBuilder().setId(firstBlogId).build());
             assertNotNull(blog);
        }
    }
}
