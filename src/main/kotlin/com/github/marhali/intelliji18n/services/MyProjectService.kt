package com.github.marhali.intelliji18n.services

import com.github.marhali.intelliji18n.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
