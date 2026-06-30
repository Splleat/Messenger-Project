package me.splleat.messengerproject.infrastructure.persistence.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.application.profile.dto.UserProfileDetailResult;
import me.splleat.messengerproject.application.profile.dto.UserProfileResult;
import me.splleat.messengerproject.common.util.StorageUrlMapper;
import me.splleat.messengerproject.domain.user.QUser;
import me.splleat.messengerproject.domain.user.QUserProfile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserProfileQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final StorageUrlMapper storageUrlMapper;

    public Optional<UserProfileDetailResult> getMyProfile(long userId) {
        QUser user = QUser.user;
        QUserProfile profile = QUserProfile.userProfile;

        UserProfileDetailResult result =  queryFactory
                .select(Projections.constructor(UserProfileDetailResult.class,
                        user.email,
                        profile.name,
                        profile.statusMessage,
                        profile.imageUrl))
                .from(profile)
                .join(user).on(profile.userId.eq(user.id))
                .where(profile.userId.eq(userId))
                .fetchOne();

        if (result != null) {
            result = result.withAbsoluteUrl(storageUrlMapper.resolve(result.imageUrl()));
        }

        return Optional.ofNullable(result);
    }

    public List<UserProfileResult> searchOtherUserProfiles(long excludeUserId, String name, int page) {
        QUser user = QUser.user;
        QUserProfile profile = QUserProfile.userProfile;

        return queryFactory
                .select(Projections.constructor(UserProfileResult.class,
                        profile.userId,
                        profile.name,
                        profile.statusMessage,
                        profile.imageUrl))
                .from(profile)
                .join(user).on(profile.userId.eq(user.id))
                .where(
                        profile.userId.ne(excludeUserId),
                        profile.name.contains(name),
                        user.deletedAt.isNull()
                )
                .offset(page * 20L)
                .limit(20)
                .fetch();
    }
}
