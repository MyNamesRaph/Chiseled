package com.mynamesraph.chiseled

import org.kodein.di.conf.ConfigurableDI
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Constants {
    const val MOD_ID = "chiseled"
    const val MOD_NAME = "Chiseled"
    val di = ConfigurableDI()

    @JvmStatic // needed so Mixins can access
    val LOG: Logger = LoggerFactory.getLogger(MOD_NAME)
}