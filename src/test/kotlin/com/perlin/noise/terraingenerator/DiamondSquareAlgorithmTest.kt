package com.perlin.noise.terraingenerator

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random

@SpringBootTest
class DiamondSquareAlgorithmTest {


    val logger: Logger = LoggerFactory.getLogger(DiamondSquareAlgorithmTest::class.java)


    val X_MAX = 1024
    val Y_MAX = 1024
    var RANDOM_SMOTHER = 0.0
    var grid: Array<DoubleArray> = Array(X_MAX + 1) { DoubleArray(Y_MAX + 1 ) { -1.0 } };

    @Test
    fun diamonSquareTest() {

        grid[0][0] = generateRandomValue(RANDOM_SMOTHER)
        grid[X_MAX][0] = generateRandomValue(RANDOM_SMOTHER)
        grid[0][Y_MAX] = generateRandomValue(RANDOM_SMOTHER)
        grid[X_MAX][Y_MAX] = generateRandomValue(RANDOM_SMOTHER)

        while (grid[1][0] == -1.0) {
            diamondStep(0, 0, X_MAX, Y_MAX)
            logger.info("BUCLE")
        }
        createImage()
        writeToFile("WRITE")
    }


    fun generateRandomValue(randomModifier: Double): Double {
        var value = Random.nextDouble()

        if (Random.nextBoolean()) value = -value

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

            grid[centerx][centery] = ((c1 + c2 + c3 + c4 + generateRandomValue(RANDOM_SMOTHER)) / 4)

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

    fun writeToFile(message:String) {
        var filename = "src/test/resources/FileDebug/array.txt"

        var file = File(filename)
        if (file.exists()) file.delete()

        file = File(filename)
        file.createNewFile()

        file.printWriter().use { out ->
            for (row in grid) {
                var rowString = ""

                for (column in row) {
                    var value = column
                    rowString += "$value "
                }
                out.println(rowString)
            }
        }
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

        grid[centerX][centerY] = ((c1 + c2 + c3 + c4 + generateRandomValue(RANDOM_SMOTHER))  / adjacentNodes)
    }

    fun createImage(){

        var filename = "src/test/resources/FileDebug/noise.png"
        // 0 ES NEGRO
        // 1 ES BLANCO
        var image = BufferedImage(X_MAX+1, Y_MAX+1, BufferedImage.TYPE_INT_RGB)
        for(x in 0..X_MAX){
            for (y in 0 ..X_MAX){

                var value = ((grid[x][y] + 1 ) * 127.5 ).toInt()
                if (value >= 255) value = 220 + Random.nextInt(35)
                if (value <= 0) value = 0 + Random.nextInt(35)


                var rbg = 0x010101 *  value

//                grid[x][y] = ((value + 1) *127.5).toInt().toDouble()
                grid[x][y] = value.toDouble()
                image.setRGB(y,x, rbg)
            }
        }
        var at: AffineTransform = AffineTransform()
        at.scale(1.0,1.0)
        var scaleOp: AffineTransformOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        var afterImage = BufferedImage(X_MAX, Y_MAX, BufferedImage.TYPE_INT_RGB)
        afterImage = scaleOp.filter(image,afterImage)

        ImageIO.write(afterImage,"png",File(filename))
    }
}