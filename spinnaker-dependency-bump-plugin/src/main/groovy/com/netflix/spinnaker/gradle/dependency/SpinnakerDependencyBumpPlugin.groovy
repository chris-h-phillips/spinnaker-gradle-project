/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gradle.dependency

import org.gradle.api.Plugin
import org.gradle.api.Project

class SpinnakerDependencyBumpPlugin implements Plugin<Project> {

  public static List<String> REPOS = [
      "clouddriver",
      "echo",
      "fiat",
      "front50",
      "gate",
      "halyard",
      "igor",
      "kayenta",
      "orca",
      "rosco",
  ]

  @Override
  void apply(Project project) {
    project.tasks.create("bump", BumpDependencies).dependsOn("updateDependencies")
    project.tasks.create("closeAllAutoBumpPRs", CloseAllAutoBumpPRs)
    project.tasks.create("mergeAllAutoBumpPRs", MergeAllAutoBumpPRs)
  }
}
