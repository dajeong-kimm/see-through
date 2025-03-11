package com.seethrough.api.common.infrastructure;

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
	 */
	public void executeAfterCommit(Runnable task) {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			log.debug("[TransactionCallbackManager] 트랜잭션 커밋 후 작업 예약");
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					log.debug("[TransactionCallbackManager] 트랜잭션 커밋 후 작업 실행");
					task.run();
				}
			});
		}
		else {
			log.debug("[TransactionCallbackManager] 활성 트랜잭션 없음. 작업 즉시 실행");
			task.run();
		}
	}
}
