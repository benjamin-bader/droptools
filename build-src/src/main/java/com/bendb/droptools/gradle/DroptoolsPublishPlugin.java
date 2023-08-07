package com.bendb.droptools.gradle;

import com.vanniktech.maven.publish.JavaLibrary;
import com.vanniktech.maven.publish.JavadocJar;
import com.vanniktech.maven.publish.MavenPublishBaseExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DroptoolsPublishPlugin implements Plugin<Project> {
    @SuppressWarnings("UnstableApiUsage")
    public void apply(Project target) {
        target.getPlugins().apply("com.vanniktech.maven.publish");

        var ext = target.getExtensions().getByType(MavenPublishBaseExtension.class);
        ext.publishToMavenCentral();

        if (isRelease(target)) {
            ext.signAllPublications();
        }

        ext.coordinates((String) target.getGroup(), (String) target.getName(), (String) target.getVersion());

        ext.pom(project -> {
            project.getName().set(target.getName());
            project.getDescription().set(target.getDescription());
            project.getUrl().set("https://github.com/benjamin-bader/droptools");

            project.licenses((licenses) -> {
                licenses.license((license) -> {
                    license.getName().set("The Apache License, Version 2.0");
                    license.getUrl().set("http://www.apache.org/licenses/LICENSE-2.0.txt");
                    license.getDistribution().set("repo");
                });
            });

            project.developers((developers) -> {
                developers.developer((developer) -> {
                    developer.getId().set("bendb");
                    developer.getName().set("Benjamin Bader");
                    developer.getUrl().set("https://www.bendb.com");
                });
            });

            project.scm(scm -> {
                scm.getUrl().set("https://github.com/benjamin-bader/droptools/");
                scm.getConnection().set("scm:git:git://github.com/benjamin-bader/droptools.git");
                scm.getDeveloperConnection().set("scm:git:ssh://git@github.com/benjamin-bader/droptools.git");
            });
        });
    }

    private static boolean isRelease(Project p) {
        Object versionName = p.findProperty("VERSION_NAME");
        if (!(versionName instanceof String)) {
            return false;
        }
        return !((String) versionName).endsWith("-SNAPSHOT");
    }
}
