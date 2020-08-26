/*
 * Copyright (c) 2020 Spigradle contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.entree.spigradle.annotations;

import com.google.auto.service.AutoService;
import lombok.SneakyThrows;
import lombok.val;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * The annotation processor for {@link SpigotPlugin}, {@link BungeePlugin}, {@link NukkitPlugin} ...
 */
@SupportedAnnotationTypes({
        "kr.entree.spigradle.annotations.SpigotPlugin",
        "kr.entree.spigradle.annotations.BungeePlugin",
        "kr.entree.spigradle.annotations.NukkitPlugin",
        "kr.entree.spigradle.annotations.PluginMain",
        "kr.entree.spigradle.annotations.Plugin"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({
        PluginAnnotationProcessor.SPIGOT_PATH_KEY,
        PluginAnnotationProcessor.BUNGEE_PATH_KEY,
        PluginAnnotationProcessor.NUKKIT_PATH_KEY
})
@AutoService(Processor.class)
public class PluginAnnotationProcessor extends AbstractProcessor {
    public static final String SPIGOT_PATH_KEY = "spigotAnnotationResultPath";
    public static final String BUNGEE_PATH_KEY = "bungeeAnnotationResultPath";
    public static final String NUKKIT_PATH_KEY = "nukkitAnnotationResultPath";
    public static final String GENERAL_PATH_KEY = "pluginAnnotationResultPath";
    private final Map<PluginType, String> resultByType = new HashMap<>();

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            for (Map.Entry<PluginType, String> entry : resultByType.entrySet()) {
                val type = entry.getKey();
                val result = entry.getValue();
                if (result.isEmpty()) continue;
                val file = new File(processingEnv.getOptions().getOrDefault(type.getPathKey(), type.getDefaultPath()));
                write(file, result).ifPresent(PluginAnnotationProcessor::error);
            }
        } else {
            Arrays.stream(PluginType.values())
                    .map(type -> type.getAnnotations().stream()
                            .map(annotation -> findClassName(roundEnv, annotation).orElse(null))
                            .filter(Objects::nonNull)
                            .findFirst()
                            .map(name -> putResult(type, name))
                            .orElse(doNothing()))
                    .forEach(Runnable::run);
        }
        return true;
    }

    private Runnable putResult(PluginType type, String result) {
        return () -> resultByType.put(type, result);
    }

    private static Optional<String> findClassName(RoundEnvironment roundEnv, Class<? extends Annotation> annotation) {
        return roundEnv.getElementsAnnotatedWith(annotation).stream()
                .filter(it -> it instanceof QualifiedNameable && it.getAnnotation(annotation) != null)
                .findFirst()
                .map(it -> ((QualifiedNameable) it).getQualifiedName().toString());
    }

    private static void error(Throwable throwable) {
        throw new IllegalStateException("Error while processing the annotations", throwable);
    }

    private static Runnable doNothing() {
        return () -> { /* Nothing */ };
    }

    private static Optional<Exception> write(File file, String contents) {
        File parent = file.getParentFile();
        if (!parent.mkdirs() && !parent.isDirectory()) {
            return Optional.of(new IllegalStateException("Couldn't create the parent directories"));
        }
        try (val writer = new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8)) {
            writer.write(contents);
        } catch (Exception ex) {
            return Optional.of(ex);
        }
        return Optional.empty();
    }
}
