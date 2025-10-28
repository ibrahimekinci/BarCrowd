package com.ibrahimekinci.barcrowd.data.mapper;

import com.ibrahimekinci.barcrowd.data.local.UserEntity;
import com.ibrahimekinci.barcrowd.domain.model.User;

public class UserMapper {
    public static UserEntity toEntity(User model) {
        if (model == null) return null;
        return new UserEntity(
                model.getId(),
                model.getEmail(),
                model.getCreatedAt()
        );
    }

    public static User toModel(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getCreatedAt()
        );
    }
}