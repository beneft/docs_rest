package com.project.repo;

import com.project.model.UserSettings;
import org.springframework.data.repository.CrudRepository;

public interface UserSettingsRepository extends CrudRepository<UserSettings, String> {
}