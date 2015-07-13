package brainslug.flow.instance;

import brainslug.flow.definition.Identifier;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface FlowInstanceTokenList {
    List<FlowInstanceToken> getActiveTokens();

    Map<Identifier, List<FlowInstanceToken>> groupedBySourceNode();

    Iterator<FlowInstanceToken> getIterator();

    void add(FlowInstanceToken token);
}
