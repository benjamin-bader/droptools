package com.bendb.droptools.gradle;

import java.util.Objects;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestExceptionFormat;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.jetbrains.annotations.NotNull;

public class DroptoolsJavaPlugin implements Plugin<Project> {

  @Override
  public void apply(@NotNull Project p) {
    applyBasePlugins(p.getPlugins());

    p.setGroup(Objects.requireNonNull(p.findProperty("GROUP")));
    p.setVersion(Objects.requireNonNull(p.findProperty("VERSION_NAME")));

    applyJavaSettings(p);
    configureTestTasks(p);
  }

  private static void applyBasePlugins(PluginContainer plugins) {
    plugins.apply(JavaLibraryPlugin.class);
    plugins.apply(IdeaPlugin.class);
  }

  private static void applyJavaSettings(Project p) {
    var settings = Objects.requireNonNull(p.getExtensions().findByType(JavaPluginExtension.class));
    settings.setSourceCompatibility(JavaVersion.VERSION_11);
    settings.setTargetCompatibility(JavaVersion.VERSION_11);

    p.getTasks().withType(JavaCompile.class).configureEach(t -> {
      t.getOptions().setFork(true);
      t.getOptions().setIncremental(true);
    });
  }

  private static void configureTestTasks(Project p) {
    p.getTasks().withType(Test.class).configureEach(task -> {
      task.useJUnitPlatform();

      task.testLogging(logging -> {
        logging.events(TestLogEvent.FAILED);
        logging.setShowExceptions(true);
        logging.setShowStackTraces(true);
        logging.setShowCauses(true);
        logging.setExceptionFormat(TestExceptionFormat.FULL);
      });
    });
  }
}
