
package com.krokodon.gradle.constantGenerator;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

public class ConstantGeneratorPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getExtensions().create("Field2ConstantPlugin", Field2ConstantExtension.class);
        project.getExtensions().create("DB2ConstantPlugin", DB2ConstantExtension.class);

        TaskProvider<Task> generateFieldConstantsTask = project.getTasks().register("generateFieldConstants", task -> {
            task.doLast(t -> new Field2ConstantTask().generateConstants(project));
        });

        //TaskProvider<Task> generateDBConstantsTask = project.getTasks().register("generateDBConstants", task -> {
        //    task.doLast(t -> new DB2ConstantTask().generateConstants(project));
        //});
        TaskProvider<DB2ConstantTask> generateDBConstantsTask = project.getTasks().register("generateDBConstants", DB2ConstantTask.class);

        project.getTasks().named("build", task -> {
            task.finalizedBy(generateFieldConstantsTask);
        });
    }
}
