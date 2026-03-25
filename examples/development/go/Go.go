package main

import (
	"fmt"
	"github.com/nicksnyder/go-i18n/v2/i18n"
)

// All examples use call argument (argument index 0)

// --- i18n.Get() — package-level call (rule-go-i18n-get) ---
// Matches: receiver = "i18n", callable = "Get"

func packageCall() {
	// Resolved keys → folding, hover documentation, and Ctrl+Click reference work
	fmt.Println(i18n.Get("common:primitive.string.sample"))
	fmt.Println(i18n.Get("user:object.deeply.nested.structure.description"))
	fmt.Println(i18n.Get("billing:object.deeply.nested.structure.description"))

	// Unresolved key → inspection error
	fmt.Println(i18n.Get("common:does.not.exist"))
}

// --- localizer.Localize() — go-i18n v2 instance call (rule-go-localizer-localize) ---
// Matches: receiver = "localizer", callable = "Localize"

func instanceCall(localizer *i18n.Localizer) {
	// Resolved keys → folding, hover documentation, and Ctrl+Click reference work
	fmt.Println(localizer.Localize("common:object.hybrid\.flat\.structure"))
	fmt.Println(localizer.Localize("billing:primitive.string.sample"))

	// Unresolved key → inspection error
	fmt.Println(localizer.Localize("user:unknown.key"))

	// Hard coded literal
	fmt.Println("Any hard coded literal")
}
