package org.cobalt.api.module.setting.impl

import org.cobalt.api.module.setting.Setting

class RangeSetting(
  name: String,
  description: String,
  defaultValue: Pair<Double, Double>,
  val min: Double,
  val max: Double
) : Setting<Pair<Double, Double>>(name, description, defaultValue)
