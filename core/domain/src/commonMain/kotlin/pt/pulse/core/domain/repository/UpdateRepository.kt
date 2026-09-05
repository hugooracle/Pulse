package pt.pulse.core.domain.repository

import pt.pulse.core.domain.data.model.update.UpdateData
import pt.pulse.core.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UpdateRepository {
    fun checkForGithubReleaseUpdate(): Flow<Resource<UpdateData>>
    fun checkForFdroidUpdate(): Flow<Resource<UpdateData>>
}