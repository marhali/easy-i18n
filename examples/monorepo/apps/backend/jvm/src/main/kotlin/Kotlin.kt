import java.util.ResourceBundle

val bundle = ResourceBundle.getBundle("messages")

// Call argument (rule-01: bundle.getString(), receiver type ResourceBundle, argument index 0)

fun resolvedKey(): String {
    // Resolved key → folding, hover documentation, and Ctrl+Click reference work
    return bundle.getString("common:primitive.string.sample")
}

fun showAll() {
    // Resolved keys → folding, hover documentation, and Ctrl+Click reference work
    println(bundle.getString("user:object.deeply.nested.structure.description"))
    println(bundle.getString("billing:object.deeply.nested.structure.description"))
    println(bundle.getString("common:object.hybrid.flat.structure"))

    // Unresolved keys → inspection error
    println(bundle.getString("common:does.not.exist"))
    println(bundle.getString("user:unknown.key"))
}

// Declaration target (rule-02: variable named "i18nKey")

fun declaredKeys() {
    // Resolved key → folding, hover documentation, and Ctrl+Click reference work
    val i18nKey = "billing:primitive.string.sample"

    // Unresolved key → inspection error
    val i18nKey2 = "common:key.that.does.not.exist"
}

// Extract translation intention — plain string literals can be extracted into a key
fun hardCoded() {
    println("An exemplary string.")
    println("A deeply nested string.")
}
