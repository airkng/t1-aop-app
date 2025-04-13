package t1.edu.utils.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import t1.edu.service.NotificationService;

import static t1.edu.utils.CommonMessages.*;

@Component
@Aspect
@RequiredArgsConstructor
public class LoggingAop {
    private final NotificationService notificationService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoggingAop.class);


    @Before("execution(* t1.edu.service.impl.TaskServiceImpl.*(..)) && @annotation(t1.edu.utils.annotations.Loggable)")
    public void loggingMethodBefore(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        //лучше уровень лога поменять на trace в будущем. Пока что дебажить неудобно
        log.info(TRACE_METHOD_CALLING, signature.toString(), args);
    }

    @After("execution(* t1.edu.service.impl.TaskServiceImpl.*(..)) && @annotation(t1.edu.utils.annotations.Loggable)")
    public void loggingMethodAfter(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        //лучше уровень лога поменять на trace в будущем. Пока что дебажить неудобно
        log.info(TRACE_METHOD_CALLING_END, signature.toString(), args);
    }

    @AfterThrowing(
            pointcut = "@annotation(t1.edu.utils.annotations.AlertException)",
            throwing = "exception"
    )
    public void sendInfo(JoinPoint joinPoint, RuntimeException exception) {
        log.error("Exception was occured in: {} .Parameters: {}", joinPoint.getSignature(), joinPoint.getArgs());
        log.error("Exception message is: {}", exception.getMessage());
        notificationService.sendExceptionMessage("Хьюстон, у нас проблемы.", exception, "delcher.dev@gmail.com");
    }

    @AfterReturning(
            pointcut = "@annotation(t1.edu.utils.annotations.HandleResult)",
            returning = "result"
    )
    public void successReturning(JoinPoint jp, Object result) {
        log.debug(HANDLE_RESULT_MESSAGE, result);
    }

    @Around("@annotation(t1.edu.utils.annotations.TestPerformance)")
    public Object testingPerformance(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        //Thread.sleep(1500); //для тестирования
        Object proceeded;
        try {
            proceeded = jp.proceed();
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw throwable;
            }
            log.error("Серьезная ошибка во время выполнения процесса: {}", jp.getSignature());
            throw throwable;
        }
        long end = System.currentTimeMillis();
        long result = end - start;
        if (result > 1500) {
            log.warn(LOW_PERFORMANCE_MESSAGE, jp.getSignature(), jp.getArgs(), result);
        } else {
            log.info(PERFORMANCE_MESSAGE, jp.getSignature(), jp.getArgs(), result);
        }
        return proceeded;
    }

    @Around("@annotation(t1.edu.utils.annotations.Loggable) && execution(* t1.edu.kafka.KafkaTaskConsumer.*(..))")
    public Object handleKafkaConsumer(ProceedingJoinPoint jp) throws Throwable {
        log.info(KAFKA_START_HANDLING_MESSAGE, jp.getSignature(), jp.getArgs()[2], jp.getArgs()[3]);
        Object proceeded;
        try {
            proceeded = jp.proceed();
        } catch (Throwable throwable) {
            if (throwable instanceof Exception) {
                throw throwable;
            }
            log.error("Серьезная ошибка во время выполнения процесса: {}", jp.getSignature());
            throw throwable;
        }
        log.info(KAFKA_END_HANDLING_MESSAGE, jp.getSignature());
        return proceeded;
    }


}
