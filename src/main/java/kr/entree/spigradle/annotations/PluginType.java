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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by JunHyung Im on 2020-08-25
 */
public enum PluginType {
    SPIGOT(
            PluginAnnotationProcessor.SPIGOT_PATH_KEY,
            "build/spigradle/spigot_main",
            SpigotPlugin.class
    ),
    BUNGEE(
            PluginAnnotationProcessor.BUNGEE_PATH_KEY,
            "build/spigradle/bungee_main",
            BungeePlugin.class
    ),
    NUKKIT(
            PluginAnnotationProcessor.NUKKIT_PATH_KEY,
            "build/spigradle/nukkit_main",
            NukkitPlugin.class
    ),
    GENERAL(
            PluginAnnotationProcessor.GENERAL_PATH_KEY,
            "build/spigradle/plugin_main",
            PluginMain.class, Plugin.class
    ),
    ;
    private final String pathKey;
    private final String defaultPath;
    private final List<Class<? extends Annotation>> annotations;

    @SafeVarargs
    PluginType(String pathKey, String defaultPath, Class<? extends Annotation>... annotations) {
        this.pathKey = pathKey;
        this.defaultPath = defaultPath;
        this.annotations = Arrays.asList(annotations);
    }

    public String getPathKey() {
        return pathKey;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public List<Class<? extends Annotation>> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }
}
