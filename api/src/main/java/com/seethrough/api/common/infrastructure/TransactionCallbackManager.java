package com.seethrough.api.common.infrastructure;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TransactionCallbackManager {

	/**
	 * 트랜잭션 커밋 후 작업을 실행합니다.
	 * 트랜잭션이 활성화되어 있지 않은 경우 즉시 실행합니다.
	 * @param runnable 트랜잭션 커밋 후 실행할 작업
	 */
	public void executeAfterCommit(Runnable runnable) {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			log.debug("[TransactionCallbackManager] 트랜잭션 커밋 후 작업 예약");
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					log.debug("[TransactionCallbackManager] 트랜잭션 커밋 후 작업 실행");
					runnable.run();
				}
			});
		}
		else {
			log.debug("[TransactionCallbackManager] 활성 트랜잭션 없음. 작업 즉시 실행");
			runnable.run();
		}
	}

	/**
	 * 트랜잭션 커밋 후 작업을 실행하고 결과를 반환하는 CompletableFuture를 제공합니다.
	 * 트랜잭션이 활성화되어 있지 않은 경우 즉시 실행합니다.
	 * @param <T> 반환 타입
	 * @param supplier 트랜잭션 커밋 후 T를 반환하는 작업
	 * @return 작업 결과를 포함한 CompletableFuture
	 */
	public <T> CompletableFuture<T> executeAfterCommit(Supplier<T> supplier) {
		CompletableFuture<T> future = new CompletableFuture<>();

		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			log.debug("[TransactionCallbackManager] 트랜잭션 커밋 후 작업 예약");

			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					try {
						log.debug("[TransactionCallbackManager] 트랜잭션 커밋 후 작업 실행");
						T result = supplier.get();
						future.complete(result);
					} catch (Exception e) {
						future.completeExceptionally(e);
					}
				}
			});
		}
		else {
			log.debug("[TransactionCallbackManager] 활성 트랜잭션 없음. 작업 즉시 실행");
			try {
				T result = supplier.get();
				future.complete(result);
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		}

		return future;
	}
}
