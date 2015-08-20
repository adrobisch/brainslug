package brainslug;

// #tag::test-service[]
public interface TestService {
  String getString();

  String echo(String echo);

  String multiEcho(String echo, String echo2);
}
// #end::test-service[]
