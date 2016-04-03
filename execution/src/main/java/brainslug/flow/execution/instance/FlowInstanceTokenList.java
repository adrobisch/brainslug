package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;

import java.util.Iterator;
import java.util.List;

public interface FlowInstanceTokenList extends Iterable<FlowInstanceToken> {
    List<FlowInstanceToken> getActiveTokens();

    List<FlowInstanceToken> getNodeTokens(Identifier nodeId);

    Iterator<FlowInstanceToken> iterator();

    void add(FlowInstanceToken token);
}
