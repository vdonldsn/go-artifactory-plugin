package com.tw.go.plugins.artifactory.task.executor;

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.EnvironmentVariables;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import com.tw.go.plugins.artifactory.ArtifactoryClient;
import com.tw.go.plugins.artifactory.Logger;

import java.io.File;
import java.io.IOException;

import static com.tw.go.plugins.artifactory.task.EnvironmentVariable.*;
import static com.tw.go.plugins.artifactory.task.config.ConfigElement.path;
import static com.tw.go.plugins.artifactory.task.config.ConfigElement.uri;
import static java.lang.String.format;

public class PublishTaskExecutor implements TaskExecutor {
    private Logger logger = Logger.getLogger(getClass());

    @Override
    public ExecutionResult execute(TaskConfig config, TaskExecutionContext context) {
        EnvironmentVariables environment = context.environment();
        String url = ARTIFACTORY_URL.from(environment).get();
        String user = ARTIFACTORY_USER.from(environment).get();
        String password = ARTIFACTORY_PASSWORD.from(environment).get();
        ArtifactoryClient client = new ArtifactoryClient(url, user, password);

        String artifactPath = config.getValue(path.name());
        String artifactUri = config.getValue(uri.name());

        try {
            client.uploadArtifact(context.workingDir() + File.separator + artifactPath, artifactUri);
        }
        catch (IOException e) {
            logger.error("Error publishing artifact", e);
            return ExecutionResult.failure("Error publishing artifact: " + e.getMessage());
        }

        return ExecutionResult.success(format("Published artifact [%s]", artifactPath));
    }
}
