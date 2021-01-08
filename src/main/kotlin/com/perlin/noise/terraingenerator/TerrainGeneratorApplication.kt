package com.terraingenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TerrainGeneratorApplication

fun main(args: Array<String>) {
	runApplication<TerrainGeneratorApplication>(*args)
}
