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

package kr.entree.spigradle.annotations.processor;

import com.google.auto.service.AutoService;
import kr.entree.spigradle.Plugin;
import kr.entree.spigradle.PluginMain;
import lombok.SneakyThrows;
import lombok.val;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The annotation processor for @PluginMain or @Plugin
 */
@SupportedAnnotationTypes({"kr.entree.spigradle.PluginMain", "kr.entree.spigradle.Plugin"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(PluginAnnotationProcessor.PLUGIN_APT_RESULT_PATH_KEY)
@AutoService(Processor.class)
public class PluginAnnotationProcessor extends AbstractProcessor {
    public static final String PLUGIN_APT_RESULT_PATH_KEY = "pluginAnnotationProcessResultPath";
    public static final String PLUGIN_APT_DEFAULT_PATH = "spigradle/plugin-main";
    public String pluginName = "";

    @SneakyThrows
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() && !pluginName.isEmpty()) {
            val file = new File(processingEnv.getOptions().getOrDefault(PLUGIN_APT_RESULT_PATH_KEY, "build/" + PLUGIN_APT_DEFAULT_PATH));
            file.getParentFile().mkdirs();
            try (val writer = new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8)) {
                writer.write(pluginName);
            }
        } else {
            pluginName = Stream.of(PluginMain.class, Plugin.class)
                    .flatMap(annotation -> roundEnv.getElementsAnnotatedWith(annotation).stream()
                            .filter(it -> it instanceof QualifiedNameable && it.getAnnotation(annotation) != null))
                    .findAny()
                    .map(it -> ((QualifiedNameable) it).getQualifiedName().toString())
                    .orElse("");
        }
        return true;
    }
}
