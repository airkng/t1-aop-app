package t1.edu.utils.aop;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import t1.edu.service.NotificationService;
import t1.edu.utils.annotations.Loggable;

import static t1.edu.utils.CommonMessages.TRACE_METHOD_CALLING;
import static t1.edu.utils.CommonMessages.TRACE_METHOD_CALLING_END;

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
        //todo: заменить уровень логирования
        log.error(TRACE_METHOD_CALLING, signature.toString(), args);
    }

    @After("execution(* t1.edu.service.impl.TaskServiceImpl.*(..)) && @annotation(t1.edu.utils.annotations.Loggable)")
    public void loggingMethodAfter(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        //todo: заменить уровень логирования
        log.error(TRACE_METHOD_CALLING_END, signature.toString(), args);
    }

    @AfterThrowing(
            pointcut = "@annotation(t1.edu.utils.annotations.AlertException)",
            throwing = "exception"
    )
    public void sendInfo(JoinPoint joinPoint, RuntimeException e) {
        log.error("Exception was occured in: {} .Parameters: {}", joinPoint.getSignature(), joinPoint.getArgs());
        log.error("Exception message is: {}", e.getMessage());
        notificationService.sendExceptionMessage("Хьюстон, у нас проблемы.", e);
    }

    @AfterReturning("")
    public void successReturning() {

    }

    @Around("@annotation(t1.edu.utils.annotations.TestPerformance)")
    public Object testingPerformance(ProceedingJoinPoint jp) {
        //todo: test performance
        Object proceeded;
        try {
            proceeded = jp.proceed();
        } catch (Throwable throwable) {
            log.error("Серьезная ошибка во время выполнения процесса: {}", jp.getSignature());
            throw new RuntimeException("Серьезная ошибка во время выполнения процесса");
        }
        return proceeded;
    }



}
