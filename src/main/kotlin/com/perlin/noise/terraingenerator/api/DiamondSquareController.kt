package com.perlin.noise.terraingenerator.api

import com.perlin.noise.terraingenerator.service.DiamondSquareAlgorithm
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.util.*


@RestController
@RequestMapping("api/v1/terrainGenerator/diamondSquare")
class DiamondSquareController {

    val logger: Logger = LoggerFactory.getLogger(DiamondSquareController::class.java)





    @GetMapping("/image")
    fun diamondSquareImage(size: Int, seed: Optional<Long>): ResponseEntity<Resource> {

        var algorithm = if (seed.isPresent) DiamondSquareAlgorithm(size, seed.get(), sendImage = true) else DiamondSquareAlgorithm(size, sendImage = true)

        algorithm.diamonSquareTest()

        var filename = DiamondSquareAlgorithm::class.java.getResource("/TempFiles/smooth.png").file

        var file = File(filename)

        var resource = ByteArrayResource(file.readBytes())



        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.IMAGE_PNG)
                .body(resource)
    }

    @GetMapping("/array")
    fun diamondSquareTxt(size: Int, seed :Optional<Long>): ResponseEntity<Resource> {

        var algorithm = if (seed.isPresent) DiamondSquareAlgorithm(size, seed.get(), sendText = true) else DiamondSquareAlgorithm(size, sendText = true)

        algorithm.diamonSquareTest()

        var filename = DiamondSquareAlgorithm::class.java.getResource("/TempFiles/smooth.txt").file

        var file = File(filename)

        var resource = ByteArrayResource(file.readBytes())

        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource)
    }
}