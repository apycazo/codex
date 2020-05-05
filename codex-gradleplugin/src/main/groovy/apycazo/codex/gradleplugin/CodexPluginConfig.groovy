package apycazo.codex.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication

class CodexPluginConfig implements Plugin<Project> {

  @Override
  void apply(Project project) {
    def config = project.extensions.create('codex', CodexPluginExtension)
    project.afterEvaluate {
      if (config.publish) publish(project)
      if (config.manifest) setManifest(project)
    }
  }

  private static publish(Project project) {
    project.apply plugin: 'maven-publish'
    project.publishing {
      publications {
        mavenJava(MavenPublication) {
          from project.components.java
        }
      }
    }
  }

  private static setManifest(Project project) {
    def manifestAttributes = generateManifest(project)
    project.jar {
      manifest.attributes(manifestAttributes)
    }
    if (project.getPlugins().findPlugin("war") != null) {
      project.war {
        manifest.attributes(manifestAttributes)
      }
    }
  }

  private static generateManifest(Project project) {
    return [
      "Build-Date"            : new Date().format("yyyy-MM-dd HH:mm:ss"),
      "Implementation-Version": project.version,
      "Specification-Version" : project.version,
      "Implementation-Title"  : project.name,
      "Specification-Title"   : project.name,
      "Automatic-Module-Name" : "$project.group.$project.name"
    ]
  }
}
