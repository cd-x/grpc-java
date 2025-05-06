package com.example.blog.impl;

import com.example.blog.*;
import com.example.blog.constants.Constants;
import com.example.blog.utils.BiDirectionalBlogDocumentMapper;
import com.google.protobuf.Empty;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Slf4j
public class BlogService extends BlogServiceGrpc.BlogServiceImplBase {
    private final MongoCollection<Document> collection;
    public BlogService(MongoClient mongoClient){
        MongoDatabase db = mongoClient.getDatabase("blogdb");
        collection = db.getCollection("blog");
    }

    @Override
    public void createBlog(Blog request, StreamObserver<BlogId> responseObserver) {
        Document document = BiDirectionalBlogDocumentMapper.mapBlogToDocument(request);
        collection.insertOne(document);
        ObjectId id = document.getObjectId(document);
        responseObserver.onNext(BlogId.newBuilder().setId(id.toString()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(BlogId request, StreamObserver<Blog> responseObserver) {
        Document document = collection.find(eq(Constants.ID, request.getId())).first();
        responseObserver.onNext(BiDirectionalBlogDocumentMapper.mapDocumentToBlog(document));
        responseObserver.onCompleted();
    }

    @Override
    public void updateBlog(UpdateRequest request, StreamObserver<Blog> responseObserver) {
        UpdateResult result = collection
                .updateOne(eq(Constants.ID, request.getId()), Updates.set(Constants.CONTENT, request.getContent()));
        log.info("Updates patched: {}", result.toString());
        responseObserver.onNext(Blog.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBlog(BlogId request, StreamObserver<Empty> responseObserver) {
        DeleteResult deleteResult = collection.deleteOne(eq(Constants.ID, request.getId()));
        log.debug("document deleted: {}", deleteResult.getDeletedCount());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void fetchAllBlogs(Empty request, StreamObserver<BlogList> responseObserver) {
        FindIterable<Document> documentList = collection.find();
        BlogList blogList = BlogList.newBuilder().addAllBlogs(documentList.map(BiDirectionalBlogDocumentMapper::mapDocumentToBlog)).build();
        responseObserver.onNext(blogList);
        responseObserver.onCompleted();
    }
}
