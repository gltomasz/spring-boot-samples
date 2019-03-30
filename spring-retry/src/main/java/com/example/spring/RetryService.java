package com.example.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
class RetryService {

    private int retryCount;
    private boolean recovered;

    private Logger logger = LoggerFactory.getLogger(RetryService.class);

    @Retryable(maxAttempts = 5, include = CustomException.class)
    void doSomeWorkAndThrowException() throws CustomException {
        retryCount++;
        throw new CustomException();
    }

    @Retryable(maxAttempts = 5, include = CustomException.class)
    void doSomeWorkAndThrowUnhandledException() throws Exception {
        retryCount++;
        throw new Exception();
    }

    @Recover()
    void recoverFromCustomException(CustomException ex) {
        logAndRecover(ex);
    }

    @Recover()
    void recoverFromGeneralException(Exception ex) {
        logAndRecover(ex);
    }

    private void logAndRecover(Exception ex) {
        logger.info("Recovered from exception {}", ex.toString());
        recovered = true;
    }

    int retryCount() {
        return retryCount;
    }

    boolean wasRecovered() {
        return recovered;
    }

    void reset() {
        retryCount = 0;
        recovered = false;
    }
}
