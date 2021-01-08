package com.terraingenerator.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.absoluteValue
import kotlin.random.Random

class DiamondSquareAlgorithm {

    var logger: Logger = LoggerFactory.getLogger(DiamondSquareAlgorithm::class.java)

    var MAX_SIZE = 128
    var SEED = 0L
    var sendImage = false
    var sendText = false

    var RANDOM_MODIFIER = 0.0
    var grid: Array<DoubleArray>


    constructor(maxSize : Int, seed : Long = Random.nextLong(4294967296), sendImage : Boolean = false, sendText : Boolean = false){
        this.SEED = seed
        this.MAX_SIZE = maxSize
        this.grid = Array(MAX_SIZE + 1) { DoubleArray(MAX_SIZE + 1 ) { -1.0 } }
        this.sendImage = sendImage
        this.sendText = sendText
    }



    fun  diamonSquareTest() {

//        logger.info("$SEED")

        grid[0][0] = generateRandomValue(RANDOM_MODIFIER).absoluteValue
        grid[MAX_SIZE][0] = generateRandomValue(RANDOM_MODIFIER).absoluteValue
        grid[0][MAX_SIZE] = generateRandomValue(RANDOM_MODIFIER).absoluteValue
        grid[MAX_SIZE][MAX_SIZE] = generateRandomValue(RANDOM_MODIFIER).absoluteValue

        logger.info("C1 : ${grid[0][0]}")
        logger.info("C2 : ${grid[MAX_SIZE][0]}")
        logger.info("C3 : ${grid[0][MAX_SIZE]}")
        logger.info("C4 : ${grid[MAX_SIZE][MAX_SIZE]}")



        while (grid[1][0] == -1.0) {
            diamondStep(0, 0, MAX_SIZE, MAX_SIZE)
        }

        val smootherIterations = (Math.sqrt(MAX_SIZE.toDouble())+1).toInt() * 3

//        createImage("noise")
//        writeToFile("noise")

        smoothTerrain(smootherIterations)

        valueTo255Format()
        if (sendImage) createImage("smooth")
        if (sendText) writeToFile("smooth")
    }


    fun generateRandomValue(randomModifier: Double): Double {
        var value = Random(SEED).nextDouble()

        if (Random(SEED).nextBoolean()) value = -value
        if (SEED % 2 == 0L) SEED++ else SEED +=3

        return value
    }

    fun diamondStep(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        var c1 = grid[minX][minY]
        var c2 = grid[maxX][minY]
        var c3 = grid[minX][maxY]
        var c4 = grid[maxX][maxY]
        var centerx = (maxX + minX)/2
        var centery = (maxY + minY)/2
        var center = grid[centerx][centery]

        if (center == -1.0) {

            grid[centerx][centery] = ((c1 + c2 + c3 + c4 + generateRandomValue(RANDOM_MODIFIER)) / 4)

            squareStep(centerx,centery,-(minX-centerx))
            return
        }

        diamondStep(minX,minY,maxX/2+minX/2,maxY/2+minY/2)

        diamondStep(maxX/2+minX/2,minY,maxX,maxY/2+minY/2)

        diamondStep(minX,maxY/2+minY/2,maxX/2+minX/2,maxY)

        diamondStep(maxX/2+minX/2,maxY/2+minY/2,maxX,maxY)
    }

    fun squareStep(centerX: Int, centerY: Int, nodeDistance: Int) {

        assignValueToNode(centerX,centerY-nodeDistance,nodeDistance)

        assignValueToNode(centerX-nodeDistance,centerY,nodeDistance)

        assignValueToNode(centerX+nodeDistance,centerY,nodeDistance)

        assignValueToNode(centerX,centerY+nodeDistance,nodeDistance)
    }

    fun assignValueToNode(centerX: Int, centerY: Int, nodeDistance: Int){
        if ((centerX < 0 || centerX >= grid.size) || (centerY < 0 || centerY >= grid.size)) return

        var adjacentNodes = 4
        var c1 = 0.0
        var c2 = 0.0
        var c3 = 0.0
        var c4 = 0.0

        if ((centerX >= 0 && centerX < grid.size) && (centerY-nodeDistance >= 0 && centerY-nodeDistance < grid.size)) c1 = grid[centerX][centerY-nodeDistance] else adjacentNodes--
        if ((centerX-nodeDistance >= 0 && centerX-nodeDistance < grid.size) && (centerY >= 0 && centerY <= grid.size)) c2 = grid[centerX-nodeDistance][centerY] else adjacentNodes--
        if ((centerX+nodeDistance >= 0 && centerX+nodeDistance < grid.size) && (centerY >= 0 && centerY <= grid.size)) c3 = grid[centerX+nodeDistance][centerY] else adjacentNodes--
        if ((centerX >= 0 && centerX < grid.size) && (centerY+nodeDistance >= 0 && centerY+nodeDistance < grid.size)) c4 = grid[centerX][centerY+nodeDistance] else adjacentNodes--

        grid[centerX][centerY] = ((c1 + c2 + c3 + c4 + generateRandomValue(RANDOM_MODIFIER))  / adjacentNodes)
    }

    fun smoothTerrain(iterations:Int){
        for (i in 0..iterations) {
            for (x in 0..MAX_SIZE) {
                for (y in 0..MAX_SIZE) {
                    // c1 c2 c3
                    // c4 c0 c5
                    // c6 c7 c8
                    var adjacentNodes = 8

                    var c0 = grid[x][y]
                    var c1 = 0.0
                    var c2 = 0.0
                    var c3 = 0.0
                    var c4 = 0.0
                    var c5 = 0.0
                    var c6 = 0.0
                    var c7 = 0.0
                    var c8 = 0.0

                    if (x == 0 || y == 0 || x == MAX_SIZE || y == MAX_SIZE) {

                        //c1
                        if (x - 1 < 0 || y - 1 < 0) adjacentNodes-- else c1 = grid[x - 1][y - 1]
                        //c2
                        if (y - 1 < 0) adjacentNodes-- else c2 = grid[x][y - 1]
                        //c3
                        if (x + 1 > MAX_SIZE || y - 1 < 0) adjacentNodes-- else c3 = grid[x + 1][y - 1]
                        //C4
                        if (x - 1 < 0) adjacentNodes-- else c4 = grid[x - 1][y]
                        //C5
                        if (x + 1 > MAX_SIZE) adjacentNodes-- else c5 = grid[x + 1][y]
                        //C6
                        if (x - 1 < 0 || y + 1 > MAX_SIZE) adjacentNodes-- else c6 = grid[x - 1][y + 1]
                        //C7
                        if (y + 1 > MAX_SIZE) adjacentNodes-- else c7 = grid[x][y + 1]
                        //C8
                        if (x + 1 > MAX_SIZE || y + 1 > MAX_SIZE) adjacentNodes-- else c8 = grid[x + 1][y + 1]
                    } else {
                        c1 = grid[x - 1][y - 1]
                        c2 = grid[x][y - 1]
                        c3 = grid[x + 1][y - 1]
                        c4 = grid[x - 1][y]
                        c5 = grid[x + 1][y]
                        c6 = grid[x - 1][y + 1]
                        c7 = grid[x][y + 1]
                        c8 = grid[x + 1][y + 1]
                    }
                    val value = (c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8) / adjacentNodes

                    grid[x][y] =  if (value < -1.0) -0.99 else value
                }
            }
        }


    }

    fun writeToFile(name:String) {
        var filename = "src/test/resources/FileDebug/$name.txt"

        var file = File(filename)
        if (file.exists()) file.delete()

        file = File(filename)
        file.createNewFile()

        file.printWriter().use { out ->
            for (row in grid) {
                var rowString = ""

                for (column in row) {
                    var value = column.toString()
                    value = value.substring(0,value.length-2)
                    rowString += "$value "

                }
                out.println(rowString)
            }
        }
    }

    fun createImage(name:String){

        var filename = "src/test/resources/FileDebug/$name.png"
        // 0 ES NEGRO
        // 1 ES BLANCO
        var image = BufferedImage(MAX_SIZE + 1, MAX_SIZE + 1, BufferedImage.TYPE_INT_RGB)
        for(x in 0..MAX_SIZE){
            for (y in 0 ..MAX_SIZE){

                var rbg = 0x010101 *  grid[x][y].toInt()


                image.setRGB(y,x, rbg)
            }
        }
        var at: AffineTransform = AffineTransform()
        at.scale(1.0,1.0)
        var scaleOp: AffineTransformOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        var afterImage = BufferedImage(MAX_SIZE, MAX_SIZE, BufferedImage.TYPE_INT_RGB)
        afterImage = scaleOp.filter(image,afterImage)

        ImageIO.write(afterImage,"png", File(filename))
    }


    fun valueTo255Format() {
        for (x in 0..MAX_SIZE) {
            for (y in 0..MAX_SIZE) {

                var value = ((grid[x][y] + 1) * 127.5).toInt()
                if (value >= 250) value = 220 + generateRandomValue(RANDOM_MODIFIER).absoluteValue.toInt() * 30
                if (value <= 5) value = 1 + generateRandomValue(RANDOM_MODIFIER).absoluteValue.toInt() * 34
                grid[x][y] = value.toDouble()
            }
        }
    }


}