package brainslug.flow.execution.async;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class ArrayListTaskStoreTest {
  @Test
  public void shouldReturnOverdueTasks() {
    ArrayListTriggerStore taskStore = new ArrayListTriggerStore();

    taskStore.storeTrigger(new AsyncTrigger().withDueDate(0));
    taskStore.storeTrigger(new AsyncTrigger().withDueDate(1));
    taskStore.storeTrigger(new AsyncTrigger().withDueDate(2));

    List<AsyncTrigger> tasks = taskStore.getTriggers(new AsyncTriggerQuery().withOverdueDate(new Date(1)));
    Assertions.assertThat(tasks).hasSize(2);
  }
}