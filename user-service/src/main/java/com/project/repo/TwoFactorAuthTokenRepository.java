package com.project.repo;

import com.project.model.TwoFactorAuthToken;
import org.springframework.data.repository.CrudRepository;

public interface TwoFactorAuthTokenRepository extends CrudRepository<TwoFactorAuthToken, String> {
}