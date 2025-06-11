package com.project.service;

import com.project.model.UserSettings;
import com.project.repo.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository repo;

    public boolean is2FAEnabled(String userId) {
        return repo.findById(userId)
                .map(UserSettings::isTwoFactorEnabled)
                .orElse(false);
    }

    public void set2FA(String userId, boolean enabled) {
        UserSettings settings = repo.findById(userId)
                .orElse(new UserSettings(userId, false));
        settings.setTwoFactorEnabled(enabled);
        repo.save(settings);
    }
}
