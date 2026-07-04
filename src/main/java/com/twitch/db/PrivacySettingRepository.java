package com.twitch.db;

import com.twitch.db.entity.PrivacySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySetting, Long> {
}
