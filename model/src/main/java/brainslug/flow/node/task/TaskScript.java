package brainslug.flow.node.task;

public class TaskScript {
    String language;
    String text;

    public TaskScript(String language, String text) {
        this.language = language;
        this.text = text;
    }

    public String getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "TaskScript{" +
                "text='" + text + '\'' +
                ", language='" + language + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskScript that = (TaskScript) o;

        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        return !(language != null ? !language.equals(that.language) : that.language != null);

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        return result;
    }
}
