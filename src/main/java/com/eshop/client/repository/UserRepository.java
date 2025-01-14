package com.eshop.client.repository;

import com.eshop.client.entity.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, UUID> {
	 Optional<UserEntity> findByUserName(String userName);
	 Optional<UserEntity> findByEmail(String email);
	 boolean existsByUid(String uid);
	Optional<UserEntity> findByUid(String uid);
	Optional<UserEntity> findByUserNameOrEmail(String userName, String email);

	@Query("SELECT u.country.name AS country,"
			+ " COUNT(u) AS count,"
			+ " (COUNT(u) * 100.0 / (SELECT COUNT(ue) FROM UserEntity ue)) AS percent"
			+ " FROM UserEntity u"
			+ " GROUP BY u.country.name"
			+ " ORDER BY COUNT(u) DESC")
	List<CountryUsers> findAllUserCountByCountry();

	@Query("""
        SELECT coalesce(COUNT(DISTINCT u),0) 
        FROM UserEntity u
        JOIN WalletEntity w ON u.id = w.user.id
        WHERE u.parent.id = :userId         
        AND w.status = com.eshop.client.enums.EntityStatusType.Active and w.transactionType = com.eshop.client.enums.TransactionType.DEPOSIT
    """)
	Integer countActiveChildrenByUserId(@Param("userId") UUID userId);
}
