## RetryListener

RetryListener可以实现在任务重试、任务执行完成、任务失败时进行回调。如果使用@RetryFunction的方式，则RetryListener需要托管到Spring容器，否则不需要。如下是一个RetryListener的实例

    @Slf4j
    @Service("orderRetryListener")
    public class OrderRetryListener implements RetryListener {
    
        @Autowired
        private OrderBusiness orderBusiness;
    
        @Override
        public void onRetry(RetryContext retryContext) {
            log.info("@RetryFunction收到重试回调，retryCount={}, args={}", retryContext.getRetryCount(), retryContext.getArgs());
        }
    
        @Override
        public void onComplete(RetryContext retryContext) {
            log.info("@RetryFunction重试任务已完成，retryCount={}, args={}", retryContext.getRetryCount(), retryContext.getArgs());
        }
    
        @Override
        public void onError(RetryContext retryContext) {
            log.info("@RetryFunction重试任务失败，retryCount={}, args={}, error={}", retryContext.getRetryCount(), retryContext.getArgs(), retryContext.getException().getMessage());
            orderBusiness.setOrderFail((Order) retryContext.getArgs());
        }
    }