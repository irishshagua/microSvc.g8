package de.zalando.spp.brands.utils

import de.zalando.spp.brands.utils.ApplicationConfig.Environment.Dev

trait EnvironmentAwareness {

  def isDev: Boolean =
    ApplicationConfig.config.deployment.environment == Dev
}
