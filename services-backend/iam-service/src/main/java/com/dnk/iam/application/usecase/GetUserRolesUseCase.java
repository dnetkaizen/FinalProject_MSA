package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.UserRoleRepositoryPort;
import com.dnk.iam.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetUserRolesUseCase {

    private final UserRoleRepositoryPort userRoleRepositoryPort;

    @Transactional(readOnly = true)
    public List<Role> execute(String userId) {
        return userRoleRepositoryPort.findRolesByUserId(userId);
    }
}
