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

        //TODO EL CASO QUE PRODUCE FALLO ES CUANDO YA ESTAN TODOS LOS VALORES DE UNA ESQUINA EN DIAMANTE DETERMINADOS, COMO SEGUIR A LA SIGUIENTE
        //PODRIA HACER UNA COMPROBACION DE TODOS LOS VALORES SI EXISTEN
        var suma = 0f;
        var minimo = 255f;
        var maximo = 0f;generateRandomValue(RANDOM_SMOTHER)


        grid[0][0] = generateRandomValue(RANDOM_SMOTHER)
        grid[X_MAX][0] = generateRandomValue(RANDOM_SMOTHER)
        grid[0][Y_MAX] = generateRandomValue(RANDOM_SMOTHER)
        grid[X_MAX][Y_MAX] = generateRandomValue(RANDOM_SMOTHER)
        //punto central para la prueba con el diamond step
//        grid[X_MAX/2][Y_MAX/2] = generateRandomValue(RANDOM_MODIFIER)
//        writeToFile("START")

        var corner00 = grid[0][0]
        var cornerX0 = grid[X_MAX][0]
        var corner0Y = grid[0][Y_MAX]
        var cornerXY = grid[X_MAX][Y_MAX]
        while (grid[1][0] == -1.0) {
            diamondStep(0, 0, X_MAX, Y_MAX)
            logger.info("BUCLE")
        }
        writeToFile("WRITE")
        createImage()
        logger.info("\nC00 $corner00 \n CX0 $cornerX0 \n C0Y $corner0Y \n CXY $cornerXY")


//        for (i in 1..10000){
//            var value =generateRandomValue(RANDOM_MODIFIER)
//
//            if (value < minimo) minimo = value
//            if (value > maximo) maximo = value
//
//            logger.info("----------$value")
//            if (RANDOM_MODIFIER > 0) RANDOM_MODIFIER -= RANDOM_SMOTHER
//            suma+=value;
//        }
//        suma /= 10000
//        logger.info("RESULTADO FINAL: $suma")
//        logger.info("MAXIMO FINAL: $maximo")
//        logger.info("MINIMO FINAL: $minimo")

    }


    fun generateRandomValue(randomModifier: Double): Double {

        var sign = Random.nextBoolean();

        var modifier = 255f * randomModifier;
        if (sign) modifier *= -1

        var randomNumber = 127f;
        var value = Random.nextDouble()
//        logger.info("$value")
        if (value < 0.1) value += 0.15
        if (value > 0.9) value -= 0.15

        return value
    }


    fun diamondStep(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        //TODO INTRODUCIR EL PASO DEL DIAMANTE DE FORMA RECURSIVA UNA VEZ FORMADA TODOS LOS CUADRADOS POSIBLES DETERMINAR TODOS LOS PUNTOS CENTRALES PARA CADA CUADRARDO
        var c1 = grid[minX][minY]
        var c2 = grid[maxX][minY]
        var c3 = grid[minX][maxY]
        var c4 = grid[maxX][maxY]
        var centerx = (maxX + minX)/2
        var centery = (maxY + minY)/2
        var center = grid[centerx][centery]

        //TODO LA CONDICION DE SALIDA ESTA JODIDA YA QUE SE QUEDA EN BUCLE INFINITO UNA VEZ LLEGADO AL 3 BUCLE
        if (center == -1.0) {
            //ASIGNAMOS VALOR AL CENTRO

//            grid[centerx][centery] = ((c1 + c2 + c3 + c4) / 4) + generateRandomValue(RANDOM_SMOTHER)
            grid[centerx][centery] = ((c1 + c2 + c3 + c4 + generateRandomValue(RANDOM_SMOTHER)) / 4)

//            writeToFile("DIAMOND")
            //TODO REVISAR DISTANCIA DE NODO
            squareStep(centerx,centery,-(minX-centerx))
            //LLAMAMOS AQUI AL SQUARE STEP
            return
        }

        diamondStep(minX,minY,maxX/2+minX/2,maxY/2+minY/2)

        diamondStep(maxX/2+minX/2,minY,maxX,maxY/2+minY/2)

        diamondStep(minX,maxY/2+minY/2,maxX/2+minX/2,maxY)

        diamondStep(maxX/2+minX/2,maxY/2+minY/2,maxX,maxY)
    }


    fun squareStep(centerX: Int, centerY: Int, nodeDistance: Int) {
        //    C1
        // C2 C0 C3
        //    C4

        //c1
        assignValueToNode(centerX,centerY-nodeDistance,nodeDistance)
        //c2
        assignValueToNode(centerX-nodeDistance,centerY,nodeDistance)
        //c3
        assignValueToNode(centerX+nodeDistance,centerY,nodeDistance)
        //c4
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
                    //var value: Char = if (column == -1) '-' else '#'
                    var value = column
                    rowString += "$value "
                }
                out.println(rowString)
            }


        }
        logger.info(message)

    }


    fun assignValueToNode(centerX: Int, centerY: Int, nodeDistance: Int){
        if ((centerX < 0 || centerX >= grid.size) || (centerY < 0 || centerY >= grid.size)) return



        var adjacentNodes = 4;
        var c1 = 0.0
        var c2 = 0.0
        var c3 = 0.0
        var c4 = 0.0


        if ((centerX >= 0 && centerX < grid.size) && (centerY-nodeDistance >= 0 && centerY-nodeDistance < grid.size)) c1 = grid[centerX][centerY-nodeDistance] else adjacentNodes--
        if ((centerX-nodeDistance >= 0 && centerX-nodeDistance < grid.size) && (centerY >= 0 && centerY <= grid.size)) c2 = grid[centerX-nodeDistance][centerY] else adjacentNodes--
        if ((centerX+nodeDistance >= 0 && centerX+nodeDistance < grid.size) && (centerY >= 0 && centerY <= grid.size)) c3 = grid[centerX+nodeDistance][centerY] else adjacentNodes--
        if ((centerX >= 0 && centerX < grid.size) && (centerY+nodeDistance >= 0 && centerY+nodeDistance < grid.size)) c4 = grid[centerX][centerY+nodeDistance] else adjacentNodes--

        grid[centerX][centerY] = ((c1 + c2 + c3 + c4 + generateRandomValue(RANDOM_SMOTHER))  / adjacentNodes)
        //writeToFile("SQUARE")


    }

    fun createImage(){

        var filename = "src/test/resources/FileDebug/noise.png"

        var image = BufferedImage(X_MAX, Y_MAX, BufferedImage.TYPE_INT_RGB)
        for(x in 0 until X_MAX){
            for (y in 0 until X_MAX){
                val value = grid[x][y]
                var rbg = 0x010101 *  ((value + 1) * 127.5).toInt()
                image.setRGB(x,y, rbg)
            }
        }
        var at: AffineTransform = AffineTransform()
        at.scale(4.0,4.0)
        var scaleOp: AffineTransformOp = AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
        var afterImage = BufferedImage(X_MAX, Y_MAX, BufferedImage.TYPE_INT_RGB)
        afterImage = scaleOp.filter(image,afterImage)

        ImageIO.write(afterImage,"png",File(filename))


    }






}