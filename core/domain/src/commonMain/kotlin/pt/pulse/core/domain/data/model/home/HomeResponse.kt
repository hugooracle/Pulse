package pt.pulse.core.domain.data.model.home

import pt.pulse.core.domain.data.model.home.chart.Chart
import pt.pulse.core.domain.data.model.mood.Mood
import pt.pulse.core.domain.utils.Resource

data class HomeResponse(
    val homeItem: Resource<ArrayList<HomeItem>>,
    val exploreMood: Resource<Mood>,
    val exploreChart: Resource<Chart>,
)