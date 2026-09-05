package pt.pulse.core.domain.data.model.home

import pt.pulse.core.domain.data.model.home.chart.Chart
import pt.pulse.core.domain.data.model.mood.Mood
import pt.pulse.core.domain.utils.Resource

data class HomeDataCombine(
    val home: Resource<Pair<String?, List<HomeItem>>>,
    val mood: Resource<Mood>,
    val chart: Resource<Chart>,
    val newRelease: Resource<List<HomeItem>>,
)