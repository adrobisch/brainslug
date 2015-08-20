package brainslug.flow.execution.node.task;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.task.TaskScript;
import brainslug.util.Option;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptExecutor {

    ScriptEngineManager manager;

    public ScriptExecutor(ScriptEngineManager manager) {
        this.manager = manager;
    }

    public Object execute(TaskScript taskScript, ExecutionContext context) {
        Option<ScriptEngine> scriptEngine = getScriptEngine(taskScript.getLanguage());

        if (scriptEngine.isPresent()) {
            return evalScript(taskScript, scriptEngine.get(), context);
        } else {
            throw new UnsupportedOperationException("could not find a script engine for " + taskScript.getLanguage());
        }
    }

    protected Object evalScript(TaskScript taskScript, ScriptEngine scriptEngine, ExecutionContext context) {
        try {
            Bindings bindings = scriptEngine.createBindings();
            bindings.put("context", context);
            return scriptEngine.eval(taskScript.getText(), bindings);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public Option<ScriptEngine> getScriptEngine(String language) {
        return Option.of(manager.getEngineByName(language));
    }
}
