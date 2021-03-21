package de.sodis.monitoring.repository

import de.sodis.monitoring.api.MonitoringApi
import de.sodis.monitoring.db.dao.ProjectDao

class ProjectRepository(
    private val monitoringApi: MonitoringApi,
    private val projectDao: ProjectDao
) {

    suspend fun loadProjects() {
        val technologies = monitoringApi.getAllProjects()
        technologies.forEach { project ->
            projectDao.insert(project = project)
        }
    }
}