package com.example.social_reel.repository;


import com.example.social_reel.entity.Reel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReelRepository extends MongoRepository<Reel, String> {

    List<Reel> findByUserId(String userId);
}
