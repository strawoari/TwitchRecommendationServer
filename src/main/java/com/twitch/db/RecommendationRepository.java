package com.twitch.db;


import com.twitch.db.entity.FavoriteRecordEntity;

import com.twitch.db.entity.MovieRecommendation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;


import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface RecommendationRepository extends ListCrudRepository<MovieRecommendation, Long> {
}


