package com.perlin.noise.terraingenerator.api

import com.perlin.noise.terraingenerator.service.PerlinNoiseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/v1/terrainGenerator")
class TerrainGeneratorController() {


    @Autowired
    lateinit var  perlinNoiseService: PerlinNoiseService


    @GetMapping("/perlinNoise")
    fun perlinNoiseTxtVersion() : String{


        return perlinNoiseService.perlinNoiseGenerator().toString()

    }


}