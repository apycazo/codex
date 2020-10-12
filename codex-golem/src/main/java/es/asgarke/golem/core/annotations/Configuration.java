package es.asgarke.golem.core.annotations;

import java.lang.annotation.*;

/**
 * Indicated the given bean is only used to provide configurations and third party beans, but it is not to be registered
 * in the catalog or be allowed as an injection parameter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {

  /**
   * Defines packages to scan using string values.
   * @return the list of strings to scan as package paths to look for bean definitions.
   */
  String[] scanPaths() default {};

  /**
   * Defined packages to scan using base classes as anchors.
   * @return the list of base classes to use. Actually, each provided class package name will be used for the scan.
   */
  Class[] scanPackages() default {};

  /**
   * Defines what property sources should be scanned to read the application properties.
   * Classpath resources might be prepended with 'classpath:'.
   * @return the list of String paths to locate the property resources to be parsed.
   */
  String[] propertySources() default {};

  /**
   * Defines property sources, which might or might not be actually found, and not been able
   * to resolve a particular location should not result in an exception.
   * @return the list of String path to (optionally) locate the resources to be parsed.
   */
  String[] optionalPropertySources() default {};

  /**
   * Imports other definitions by providing the exact classes instead of depending on a package component scan.
   * @return the list of classes to be imported into the context.
   */
  Class<?>[] importDefinitions() default {};
}
