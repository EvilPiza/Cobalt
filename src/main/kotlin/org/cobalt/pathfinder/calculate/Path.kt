package org.cobalt.pathfinder.calculate

import kotlin.time.Duration

data class Path(
    val nodes: List<PathNode>,
    val timeElapsed: Duration
)
