package brainslug.spring;

import brainslug.flow.execution.async.AsyncTriggerExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SpringAsyncTriggerExecutor extends AsyncTriggerExecutor {
}
