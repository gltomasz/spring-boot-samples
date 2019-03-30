package com.example.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AppConfig.class)
public class RetryTest {

    private Logger logger = LoggerFactory.getLogger(RetryTest.class);

    @Autowired
    private RetryService retryService;

    @Before
    public void beforeEach() {
        retryService.reset();
    }

    @Test
    public void shouldRetryFiveTimesAndRecover() throws CustomException {
        //when
        retryService.doSomeWorkAndThrowException();
        //then
        assertThat(retryService.retryCount()).isEqualTo(5);
        //and
        assertThat(retryService.wasRecovered()).isTrue();
    }

    @Test
    public void shouldNotRetryIfExceptionTypeIsNotHandled() throws Exception {
        //when
        retryService.doSomeWorkAndThrowUnhandledException();
        //then
        assertThat(retryService.retryCount()).isEqualTo(0);
        //and
        assertThat(retryService.wasRecovered()).isTrue();
    }

    @Test
    public void shouldBeRetriedTwoTimesAfterTenSeconds() {
        //when
        doSomeWorkAndThrowException();
        //then
        await().atLeast(10, TimeUnit.SECONDS)
                .atMost(11, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(retryService.retryCount()).isEqualTo(2));
    }

    private void doSomeWorkAndThrowException() {
        Runnable task = () -> {
            try {
                retryService.doSomeWorkAndRetryAfterFiveSeconds();
            } catch (CustomException e) {
                logger.error(e.getMessage());
            }
        };
        new Thread(task).start();
    }
}
