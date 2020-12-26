package com.perlin.noise.terraingenerator

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import kotlin.random.Random

@SpringBootTest
class DiamondSquareAlgorithmTest {


    val logger: Logger = LoggerFactory.getLogger(DiamondSquareAlgorithmTest::class.java)
    var RANDOM_MODIFIER = 1F

    val X_MAX = 10
    val Y_MAX = 10
    var RANDOM_SMOTHER = 0.01F
    var grid: Array<IntArray> = Array(X_MAX + 1) { IntArray(Y_MAX + 1 ) { -1 } };

    @Test
    fun diamonSquareTest() {

        //TODO ARRAY WITHIN AN ARRAY
        var suma = 0f;
        var minimo = 255f;
        var maximo = 0f;


        grid[0][0] = generateRandomValue(RANDOM_MODIFIER)
        grid[X_MAX][0] = generateRandomValue(RANDOM_MODIFIER)
        grid[0][Y_MAX] = generateRandomValue(RANDOM_MODIFIER)
        grid[X_MAX][Y_MAX] = generateRandomValue(RANDOM_MODIFIER)
        //punto central para la prueba con el diamond step
        grid[X_MAX/2][Y_MAX/2] = generateRandomValue(RANDOM_MODIFIER)


        var corner00 = grid[0][0]
        var cornerX0 = grid[X_MAX][0]
        var corner0Y = grid[0][Y_MAX]
        var cornerXY = grid[X_MAX][Y_MAX]

        diamondStep(0, 0, X_MAX, Y_MAX)
        writeToFile()

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


    fun generateRandomValue(randomModifier: Float): Int {

        var sign = Random.nextBoolean();

        var modifier = 32f * randomModifier;
        if (sign) modifier *= -1

        var randomNumber = 32f;

        return (Random.nextFloat() * (randomNumber + modifier)).toInt()
    }

//CORNER MAP
//c1  c2
//c3  c4

    fun diamondStep(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        //TODO INTRODUCIR EL PASO DEL DIAMANTE DE FORMA RECURSIVA UNA VEZ FORMADA TODOS LOS CUADRADOS POSIBLES DETERMINAR TODOS LOS PUNTOS CENTRALES PARA CADA CUADRARDO
        var c1 = grid[minX][minY]
        var c2 = grid[maxX][minY]
        var c3 = grid[minX][maxY]
        var c4 = grid[maxX][maxY]
        var centerx = (maxX + minX)/2
        var centery = (maxY + minY)/2
        var center = grid[centerx][centery]


        if (center == -1) {
            //ASIGNAMOS VALOR AL CENTRO

            grid[centerx][centery] = ((c1 + c2 + c3 + c4) / 4) + generateRandomValue(RANDOM_SMOTHER)
            //LLAMAMOS AQUI AL SQUARE STEP
            return
        }



        //upper left square
        diamondStep(minX,minY,maxX/2,maxY/2)

        //upper right square
        diamondStep(maxX/2,minY,maxX,maxY/2)

        //lower left square
        diamondStep(minX,maxY/2,maxX/2,maxY)

        //lower right square
        diamondStep(maxX/2,maxY/2,maxX,maxY)

        return

    }


    fun squareStep() {
    //TODO COGEMOS EL PUNTO CENTRAL PRIMERO, Y DE ESO DETERMINAMOS LOS 4 PUNTOS QUE PUEDEN TENER O NO VALOR MAXX/2 MINY, MINX MAXY/2, MAXX/2 MAXY, MAXX MAXY/2
    //TODO TENER EN CUENTA QUE HAY QUE COMPROBAR SI EXTIENDE LA LA LONGITUD O ALTURA DEL ARRAY YA QUE EN ESE CASO SE OBVIA

    //TODO SI TIENEN VALOR PASAMOS A LAS DIAGONALES DE CADA SUBCUADRADO

    }

    fun writeToFile() {
        var filename = "src/test/resources/FileDebug/array.txt"


        var file = File(filename)
        if (file.exists()) file.delete()

        file = File(filename)
        file.createNewFile()


        file.printWriter().use { out ->
            for (row in grid) {
                var rowString = ""

                for (column in row) {
                    var value: Char = if (column == -1) '-' else '#'
                    rowString += "$value "
                }
                out.println(rowString)
            }


        }


    }



}