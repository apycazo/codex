package es.asgarke.golem.core.dummy;

import es.asgarke.golem.core.GolemContext;
import es.asgarke.golem.core.annotations.Configuration;
import es.asgarke.golem.core.annotations.NonRequired;
import es.asgarke.golem.core.annotations.PropertyValue;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * This is a simple service to test basic features:
 * <li>The @Configuration scans for the current package, and imports the required property file</li>
 * <li>The @Singleton marks this class as a singleton too, so it will be instanced as such later</li>
 * <li>Unnamed field injection works (blue)</li>
 * <li>Named field injection works (optionOne)</li>
 * <li>Prototype field injection works (smith)</li>
 * <li>Unnamed constructor injection works (red)</li>
 * <li>Named constructor injection works (optionTwo)</li>
 * <li>Prototype constructor injection works (ash)</li>
 * <li>Property field injection works, when the property is found (masterName, initiateName)</li>
 * <li>Property field injection works, when the property is not found (agentName)</li>
 * <br>
 * Notice that this class has a dual behavior, on one side it is a configuration class, which will be used just to
 * know which packages are to be scanned, to generate one bean, load property files to use, etc. On the other side, it
 * is also marked as a singleton, so it will be scanned as one too. This is meant for simplicity here.
 */
@Getter
@Singleton
@Configuration(scanPackages = MatrixService.class, propertySources = "classpath:test.properties")
public class MatrixService {

  @Inject
  private GolemContext context;

  @Inject
  @Named(RedPill.NAME)
  private Pill optionOne;
  private Pill optionTwo;
  @Inject
  @Named("greenPill")
  private Pill green;

  @Inject
  private BluePill blue;
  private RedPill red;

  @Inject
  private Agent smith;
  private Agent ash;

  @Inject
  @NonRequired
  private BlackCat blackCat;

  @PropertyValue("master.name:unknown")
  private String masterName;
  @PropertyValue("initiate.name:unknown")
  private String initiateName;
  @PropertyValue("agent.name:unknown")
  private String agentName;
  private String appName;

  /**
   * This constructor is required since this is annotated as @Configuration AND has methods creating beans.
   */
  public MatrixService() {}

  /**
   * This constructor will be used to instance the singleton scope of this class
   * @param optionTwo an interface injection, with the required name.
   * @param red an injection of a simple bean (singleton).
   * @param ash an injection of a prototype bean.
   */
  @Inject
  public MatrixService(@Named(BluePill.NAME) Pill optionTwo, RedPill red, Agent ash,
                       @PropertyValue("app.name:default") String applicationName) {
    this.optionTwo = optionTwo;
    this.red = red;
    this.ash = ash;
    this.appName = applicationName;
  }

  @Singleton
  public Pill greenPill() {
    return new Pill() {
      @Override
      public Color color() {
        return Color.GREEN;
      }

      @Override
      public boolean take() {
        return false;
      }
    };
  }
}
