package brainslug.flow.execution.node.task;

import brainslug.flow.context.ExecutionContext;
import brainslug.flow.node.task.TaskScript;
import brainslug.util.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;

public class ScriptExecutor {

    ScriptEngineManager manager;
    Logger log = LoggerFactory.getLogger(ScriptExecutor.class);

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
            StringWriter outputWriter = new StringWriter();
            StringWriter errorWriter = new StringWriter();


            ScriptContext scriptContext = scriptEngine.getContext();
            scriptContext.setWriter(outputWriter);
            scriptContext.setErrorWriter(errorWriter);

            scriptContext.setAttribute("context", context, ScriptContext.ENGINE_SCOPE);

            Object result = scriptEngine.eval(taskScript.getText(), scriptContext);

            log.debug("script-out", "script output: " + writerContent(outputWriter));
            log.debug("script-err", "error output: " + writerContent(errorWriter));

            return result;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private String writerContent(StringWriter outputWriter) {
        outputWriter.flush();
        return outputWriter.getBuffer().toString();
    }

    public Option<ScriptEngine> getScriptEngine(String language) {
        return Option.of(manager.getEngineByName(language));
    }
}
