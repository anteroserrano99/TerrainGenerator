package com.perlin.noise.terraingenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TerrainGeneratorApplication

fun main(args: Array<String>) {
	System.setProperty("spring.devtools.restart.enabled", "false");
	runApplication<TerrainGeneratorApplication>(*args)
}
