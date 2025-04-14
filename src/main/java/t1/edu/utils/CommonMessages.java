package t1.edu.utils;

public class CommonMessages {
    public static final String TASK_NOT_FOUND_MESSAGE = "Task with id = %s was not found";
    public static final String USER_NOT_FOUND_MESSAGE = "User with id = %s was not found";
    public static final String TASK_ALREADY_EXISTS_MESSAGE = "Task with params: %s already exists";
    public static final String TRACE_METHOD_CALLING = "Starting to call method: {} with params: {} ";
    public static final String TRACE_METHOD_CALLING_END = "Ending to call method: {} with params: {} ";
    public static final String PERFORMANCE_MESSAGE = "Performance of method: {} with params: {}. Result: {} ms";
    public static final String LOW_PERFORMANCE_MESSAGE = "Performance of method: {} with params: {} is LOW. Result: {} ms";
    public static final String HANDLE_RESULT_MESSAGE = "Success execution of method. Result: {}";
    public static final String KAFKA_START_HANDLING_MESSAGE = "Start handling kafka messages from {}. Topic: {}, key: {}";
    public static final String KAFKA_END_HANDLING_MESSAGE = "End of handling kafka messages from {}.";
    public static final String KAFKA_START_PRODUCING_MESSAGE = "Start produce kafka messages from {}. Value: {}.";
    public static final String KAFKA_END_PRODUCING_MESSAGE = "End of produce kafka messages from {}.";
}
