package brainslug.flow.execution.node;

import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.token.Token;
import brainslug.flow.execution.token.TokenList;
import brainslug.flow.node.FlowNodeDefinition;
import brainslug.util.Option;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static org.assertj.core.api.Assertions.assertThat;

public class FlowNodeExecutionResultTest {
    @Test
    public void shouldAddTokenRemovalForIncomingTokens() {
        // given:
        FlowNodeDefinition executedNode = new FlowNodeDefinition<FlowNodeDefinition>().id("node");
        FlowNodeExecutionResult flowNodeExecutionResult = new FlowNodeExecutionResult(executedNode);

        TokenList flowInstanceTokens = new TokenList();
        flowInstanceTokens.add(new Token(id("1"), executedNode.getId(), Option.<Identifier>empty(), id("instance"), false, true));

        // when:
        flowNodeExecutionResult.withFirstIncomingTokensRemoved(flowInstanceTokens);

        // then:
        assertThat(flowNodeExecutionResult.getRemovedTokens()).contains(new FlowNodeExecutionResult.TokenRemoval(executedNode.getId(), Option.<Identifier>empty(), 1));
    }
}