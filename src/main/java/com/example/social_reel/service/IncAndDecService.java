package com.example.social_reel.service;


import com.example.social_reel.entity.Reel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class IncAndDecService {

    private final MongoTemplate mongoTemplate;




    public void incrementLike(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("likes",1);

        mongoTemplate.updateFirst(query,update, Reel.class);

    }


    public void incrementComment(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("comments",1);

        mongoTemplate.updateFirst(query,update, Reel.class);

    }

    public void decrementLike(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("likes",-1);

        mongoTemplate.updateFirst(query,update, Reel.class);

    }

    public void decrementComment(String postId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(postId));

        Update update = new Update();
        update.inc("comments",-1);

        mongoTemplate.updateFirst(query,update, Reel.class);

    }

}
