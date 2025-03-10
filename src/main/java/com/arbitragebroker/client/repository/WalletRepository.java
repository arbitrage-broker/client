package com.arbitragebroker.client.repository;

import com.arbitragebroker.client.entity.WalletEntity;
import com.arbitragebroker.client.enums.EntityStatusType;
import com.arbitragebroker.client.enums.TransactionType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Repository
public interface WalletRepository extends BaseRepository<WalletEntity, Long> {
//	@Query("SELECT coalesce( "
//			+ "SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.DEPOSIT OR w.transactionType = com.arbitragebroker.client.enums.TransactionType.REWARD OR w.transactionType = com.arbitragebroker.client.enums.TransactionType.BONUS THEN w.amount ELSE 0 END) - "
//			+ "SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL OR w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL_PROFIT THEN w.amount ELSE 0 END),0) "
//			+ "FROM WalletEntity w WHERE w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active")
//	BigDecimal totalBalance(UUID userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
	@Query("select w from WalletEntity w where w.user.id = :userId and w.status = com.arbitragebroker.client.enums.EntityStatusType.Active")
	List<WalletEntity> findAllActiveByUserId(UUID userId);

	default BigDecimal calculateUserBalance(UUID userId) {
		List<WalletEntity> list = findAllActiveByUserId(userId);

		BigDecimal deposits = list.parallelStream()
				.filter(w -> EnumSet.of(TransactionType.DEPOSIT, TransactionType.REWARD, TransactionType.BONUS)
						.contains(w.getTransactionType()))
				.map(WalletEntity::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal withdrawals = list.parallelStream()
				.filter(w -> EnumSet.of(TransactionType.WITHDRAWAL, TransactionType.WITHDRAWAL_PROFIT)
						.contains(w.getTransactionType()))
				.map(WalletEntity::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return deposits.subtract(withdrawals);
	}
	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.client.enums.TransactionType.DEPOSIT) 
			AND w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalDeposit(UUID userId);

	@Query("""
			SELECT coalesce(
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.DEPOSIT THEN w.amount ELSE 0 END) - 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w WHERE w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalBalance(UUID userId);

	@Query("""
			SELECT coalesce(
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.DEPOSIT THEN w.amount ELSE 0 END) - 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w join w.user u WHERE u.parent.id = :parentId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalBalanceOfSubUsers(UUID parentId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL OR w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL_PROFIT)
			AND w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalWithdrawal(UUID userId);
	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL_PROFIT)
			AND w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalWithdrawalProfit(UUID userId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
	@Query("""
			SELECT w 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.client.enums.TransactionType.REWARD_REFERRAL) 
			AND w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	List<WalletEntity> findAllReferralRewardByUserId(UUID userId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.client.enums.TransactionType.BONUS) 
			AND w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalBonus(UUID userId);

	@Query("""
			SELECT coalesce(SUM(w.amount),0) 
			FROM WalletEntity w WHERE (w.transactionType = com.arbitragebroker.client.enums.TransactionType.REWARD) 
			AND w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalReward(UUID userId);

	@Query("""
			SELECT coalesce( 
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.REWARD OR w.transactionType = com.arbitragebroker.client.enums.TransactionType.REWARD_REFERRAL OR w.transactionType = com.arbitragebroker.client.enums.TransactionType.BONUS THEN w.amount ELSE 0 END) -
			SUM(CASE WHEN w.transactionType = com.arbitragebroker.client.enums.TransactionType.WITHDRAWAL_PROFIT THEN w.amount ELSE 0 END),0) 
			FROM WalletEntity w WHERE w.user.id = :userId AND w.status=com.arbitragebroker.client.enums.EntityStatusType.Active""")
	BigDecimal totalProfit(UUID userId);

	long countAllByUserIdAndTransactionTypeAndStatus(UUID userId, TransactionType transactionType, EntityStatusType status);

	@Query("""
			SELECT COUNT(w) FROM WalletEntity w
			WHERE w.user.id = :userId
			AND w.transactionType = :transactionType
			AND w.status = :status
			AND w.amount = :amount
			AND CAST(w.createdDate AS date) = :date""")
	long countAllByUserIdAndTransactionTypeAndStatusAndAmountAndCreatedDate(
			UUID userId,
			TransactionType transactionType,
			EntityStatusType status,
			BigDecimal amount,
			LocalDate date);

//	@Query(value = "SELECT currency, SUM(totalAmount) AS totalAmount" +
//				   " FROM (" +
//				   " SELECT w.currency AS currency, SUM(w.amount) AS totalAmount" +
//				   " FROM tbl_wallet w" +
//				   " WHERE w.transaction_type = 'BONUS' AND w.user_id = :userId AND w.active = TRUE" +
//				   " GROUP BY w.currency" +
//				   " UNION ALL" +
//				   " SELECT a.currency AS currency, SUM(a.reward) AS totalAmount" +
//				   " FROM tbl_arbitrage a" +
//				   " WHERE a.user_id = :userId" +
//				   " GROUP BY a.currency" +
//				   " ) AS combined" +
//				   " GROUP BY currency;", nativeQuery = true)
//	List<Object[]> totalProfit(UUID userId);
}
