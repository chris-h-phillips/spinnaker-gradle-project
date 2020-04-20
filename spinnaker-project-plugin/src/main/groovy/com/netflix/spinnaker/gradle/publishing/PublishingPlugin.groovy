package com.netflix.spinnaker.gradle.publishing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.tasks.GenerateModuleMetadata

class PublishingPlugin implements Plugin<Project> {

  public static final String PUBLICATION_NAME = "spinnaker"

  @Override
  void apply(Project project) {
    project.plugins.withType(JavaLibraryPlugin) {
      project.plugins.apply(MavenPublishPlugin)
      project.logger.info "adding maven publication for java library in $project.name"
      project.extensions.configure(PublishingExtension) { publishingExtension ->
        publishingExtension.publications.create(PUBLICATION_NAME, MavenPublication) { pub ->
          pub.from(project.components.getByName("java"))
          project.tasks.matching { it.name == 'sourceJar' }.configureEach {
            pub.artifact(it)
          }
        }
      }
    }

    project.plugins.withType(JavaPlatformPlugin) {
      project.plugins.apply(MavenPublishPlugin)
      project.logger.info "adding maven publication for java platform in $project.name"
      project.extensions.configure(PublishingExtension) { publishingExtension ->
        publishingExtension.publications.create(PUBLICATION_NAME, MavenPublication) { pub ->
          pub.from(project.components.getByName("javaPlatform"))
        }
      }
      // Presence of a module metadata file causes some weird failures in kork spinnaker-dependencies and we don't
      //  really need them, so just disabling for now for javaPlatform modules
      project.tasks.named("generateMetadataFileFor${PUBLICATION_NAME.capitalize()}Publication", GenerateModuleMetadata) {
        it.enabled = false
      }
    }

    // Provide a shim to the previous entrypoint for publishing so that buildscripts can work against 7.x and 8.+
    // versions of the gradle project.
    //
    // We can remove this once we aren't maintaining release branches on the 7.x gradle plugin
    project.plugins.withType(org.gradle.api.publish.plugins.PublishingPlugin) {
      if (Boolean.valueOf(project.findProperty("enablePublishing") as String)) {
        for (shimTaskName in ["candidate", "final"]) {
          project.tasks.register(shimTaskName) {
            it.dependsOn(org.gradle.api.publish.plugins.PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME)
          }
        }
      }
    }
  }
}