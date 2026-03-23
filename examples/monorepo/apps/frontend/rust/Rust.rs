use i18n::t;
use i18n::localizer::Localizer;

// All examples use call argument (argument index 0)

// --- t() — free function shorthand (rule-rust-t) ---

// Resolved keys → folding, hover documentation, and Ctrl+Click reference work
fn free_function_calls() {
    println!("{}", t("common:primitive.string.sample"));
    println!("{}", t("user:object.deeply.nested.structure.description"));
    println!("{}", t("billing:object.deeply.nested.structure.description"));
    println!("{}", t("common:object.hybrid\.flat\.structure"));

    // Unresolved keys → inspection error
    println!("{}", t("common:does.not.exist"));
    println!("{}", t("user:unknown.key"));
}

// --- localizer.get() — instance method call (rule-rust-localizer-get) ---
// Matches: receiver = "localizer", callable = "get"

fn method_call(localizer: &Localizer) {
    // Resolved keys → folding, hover documentation, and Ctrl+Click reference work
    println!("{}", localizer.get("billing:primitive.string.sample"));
    println!("{}", localizer.get("common:primitive.string.sample"));

    // Unresolved key → inspection error
    println!("{}", localizer.get("user:unknown.key"));
}
