package ru.iandreyshev.payfriends.domain.computationsList

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetComputationsListUseCase
@Inject constructor(
    private val storage: Storage
) {

    data class Filter(
        val isCompleted: Boolean
    )

    operator fun invoke(filter: Filter): Flow<List<ComputationSummary>> {
        return storage.observable()
            .map { list ->
                list.filter { it.isCompleted != filter.isCompleted }
                    .map { computation ->
                        ComputationSummary(
                            id = computation.id,
                            title = computation.title
                        )
                    }
            }
    }

}
