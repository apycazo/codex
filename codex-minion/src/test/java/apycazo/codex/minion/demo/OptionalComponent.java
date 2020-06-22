package apycazo.codex.minion.demo;

import apycazo.codex.minion.context.conditions.OnPropertyCondition;

import javax.inject.Singleton;

@Singleton
@OnPropertyCondition(value = "application.enableOptional", matching = "true")
public class OptionalComponent {
}
