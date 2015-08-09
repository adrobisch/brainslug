package brainslug.jpa;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.expression.ContextPredicate;
import brainslug.flow.execution.node.task.SimpleTask;
import brainslug.flow.expression.PredicateExpression;
import brainslug.flow.expression.Property;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.TaskDefinition;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static brainslug.flow.execution.property.ExecutionProperties.newProperties;

public class CreateInstanceIT extends AbstractDatabaseTest {

  @Autowired
  BrainslugContext brainslugContext;

  @Test
  public void shouldCreateInstanceWithJpaStores() {
    brainslugContext.addFlowDefinition(new TestFlow().getDefinition());

    brainslugContext.startFlow(TestFlow.id, newProperties()
      .with(TestFlow.emailAddress, "user@localhost"));
  }

  public static class TestFlow extends FlowBuilder {

    public static Identifier id = id("forgot_password");

    public static Property<String> emailAddress = property(id("email"), String.class);
    public static Property<String> username = property(id("username"), String.class);
    public static Property<String> confirmationCode = property(id("code"), String.class);

    public static final Identifier confirmationReceivedId = id("confirmationReceivedId");
    public EventDefinition confirmationReceived = event(confirmationReceivedId);
    public static EventDefinition passwordRequest = event(id("password_request"));
    public EventDefinition invalidEmail = event(id("invalid_email"));

    TaskDefinition sendConfirmationCode = task(id("send_confirmation_mail"), new SimpleTask() {
      @Override
      public void execute(ExecutionContext context) {

      }
    });
    TaskDefinition sendNewPassword = task(id("send_new_password"), new SimpleTask() {
      @Override
      public void execute(ExecutionContext context) {

      }
    });

    @Override
    public void define() {
      flowId(id);

      start(passwordRequest)
        .choice(id("user_exists"))
        .when(userExists())
        .execute(sendConfirmationCode)
        .waitFor(confirmationReceived)
        .execute(sendNewPassword)
        .otherwise()
        .end(invalidEmail);
    }

    private PredicateExpression userExists() {
      return predicate(new ContextPredicate() {
        @Override
        public boolean isFulfilled(ExecutionContext context) {
          context.setProperty(TestFlow.username.getValue().stringValue(), "username");

          return true;
        }
      });
    }

  }

}
