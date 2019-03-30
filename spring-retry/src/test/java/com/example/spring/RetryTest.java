package com.example.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AppConfig.class)
public class RetryTest {

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
        assertThat(retryService.retryCount()).isEqualTo(1);
        //and
        assertThat(retryService.wasRecovered()).isTrue();
    }
}
