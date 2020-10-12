package es.asgarke.golem.core.dummy;

import es.asgarke.golem.core.annotations.ConditionalOnProperty;

import javax.inject.Singleton;

@Singleton
@ConditionalOnProperty(value = "cat.present", expectedValue = "true")
public class BlackCat {
}
