import java.util.ResourceBundle;

public class Java {

    private final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    // Call argument (rule-01: bundle.getString(), receiver type ResourceBundle, argument index 0)

    public String resolvedKey() {
        // Resolved key → folding, hover documentation, and Ctrl+Click reference work
        return bundle.getString("common:primitive.string.sample");
    }

    public void showAll() {
        // Resolved keys → folding, hover documentation, and Ctrl+Click reference work
        System.out.println(bundle.getString("user:object.deeply.nested.structure.description"));
        System.out.println(bundle.getString("billing:object.deeply.nested.structure.description"));
        System.out.println(bundle.getString("common:object.hybrid\\.flat\\.structure"));

        // Unresolved keys → inspection error
        System.out.println(bundle.getString("common:does.not.exist"));
        System.out.println(bundle.getString("user:unknown.key"));
    }

    // Extract translation intention — plain string literals can be extracted into a key
    public void hardCoded() {
        System.out.println("An exemplary string.");
        System.out.println("A deeply nested string.");
    }
}
