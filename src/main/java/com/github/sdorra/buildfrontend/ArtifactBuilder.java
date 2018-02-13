package com.github.sdorra.buildfrontend;

import com.google.common.base.Strings;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.repository.RepositorySystem;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ArtifactBuilder {

    private static final String GROUPID = "com.github.sdorra";

    private final RepositorySystem repositorySystem;

    @Inject
    public ArtifactBuilder(RepositorySystem repositorySystem) {
        this.repositorySystem = repositorySystem;
    }

    public ArtifactBuilderFinalizer builder(String artifactId, String version, String packaging) {
        return new ArtifactBuilderFinalizer(repositorySystem, artifactId, version, packaging);
    }

    public class ArtifactBuilderFinalizer {

        private RepositorySystem repositorySystem;
        private String artifactId;
        private String version;
        private String packaging;
        private String classifier;

        private ArtifactBuilderFinalizer(RepositorySystem repositorySystem, String artifactId, String version, String packaging) {
            this.repositorySystem = repositorySystem;
            this.artifactId = artifactId;
            this.version = version;
            this.packaging = packaging;
        }

        public ArtifactBuilderFinalizer withClassifier(String classifier) {
            this.classifier = classifier;
            return this;
        }

        public Artifact build() {
            if (Strings.isNullOrEmpty(classifier)) {
                return buildWithoutClassifier();
            }
            return buildWithClassifier();
        }

        private Artifact buildWithoutClassifier() {
            return repositorySystem.createArtifact(
                    GROUPID,
                    artifactId,
                    version,
                    packaging
            );
        }

        private Artifact buildWithClassifier() {
            return repositorySystem.createArtifactWithClassifier(
                    GROUPID,
                    artifactId,
                    version,
                    packaging,
                    classifier
            );
        }
    }

}
