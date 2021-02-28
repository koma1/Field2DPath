package pw.komarov.field2dpath.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pw.komarov.field2dpath.Field2DPath;

import static org.junit.jupiter.api.Assertions.*;

class Field2DPathTest {
    private Field2DPath field2DPath;

    @BeforeEach
    void init() {
        field2DPath = new Field2DPath(10, 20).setBrick(3, 5) //10x20 rectangle field
                .setBrick(4,5)
                .setBrick(5,5)
                .setBrick(5,4)
                .setBrick(3,4)

                .setBrick(1, 15)
                .setBrick(1, 14)
                .setBrick(2, 14)
                .setBrick(3, 14)
                .setBrick(3, 15)
                .setRoutePoints(2,15, 4,4)
                .calculate();
    }

    @Test
    void mainTest() {
        //initial way test
        System.out.println("Testing initial calculation:" + field2DPath);
        assertEquals(
                "[1(2;16), 2(3;16), 3(4;16), 4(4;15), 5(4;14), 6(4;13), 7(4;12), 8(4;11), 9(4;10), 10(4;9), 11(4;8), 12(4;7), 13(4;6), 14(3;6), 15(2;6), 16(2;5), 17(2;4), 18(2;3), 19(3;3), 20(4;3)]",
                field2DPath.getPath().toString()
        );

        //добавлением препятствия изменим оптимальное направление с лева на право (change the way from left to right)
        field2DPath.setBrick(3, 3).calculate();
        System.out.println("Way changed (by brick added) from left to right:" + field2DPath); //...and show it in console
        assertEquals(
                "[1(2;16), 2(3;16), 3(4;16), 4(4;15), 5(4;14), 6(4;13), 7(4;12), 8(4;11), 9(4;10), 10(4;9), 11(4;8), 12(4;7), 13(4;6), 14(5;6), 15(6;6), 16(6;5), 17(6;4), 18(6;3), 19(5;3), 20(4;3)]",
                field2DPath.getPath().toString()
        );

        //("закроем" точку "B")...and "close" B point
        field2DPath.setBrick(4, 3).calculate();
        System.out.println("Closed 'B' point:\n" + field2DPath); //...and show it in console
        assertTrue(field2DPath.isPointsIsolated());
    }

    @Test
    void throwsTest() {
        //collisions
        assertThrows(Field2DPath.CollisionException.class, () -> field2DPath.setBrick(2,15)); //try set brick to A point
        assertThrows(Field2DPath.CollisionException.class, () -> field2DPath.setBrick(4,4)); //try set brick to B point
        assertThrows(Field2DPath.CollisionException.class, () -> field2DPath.setRoutePoints(1,15, 3, 16)); //try set A point to brick
        assertThrows(Field2DPath.CollisionException.class, () -> field2DPath.setRoutePoints(2,17, 1, 14)); //try set B point to brick
        //bounds
        assertDoesNotThrow(() -> field2DPath.setBrick(1,1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> field2DPath.setBrick(1,0));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> field2DPath.setBrick(0,1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> field2DPath.setBrick(25,1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> field2DPath.setBrick(1,25));
    }

    @Test
    void brickSetUnsetIssetTest() {
        assertFalse(field2DPath.isBrick(1, 1));
        field2DPath.setBrick(1,1);
        assertTrue(field2DPath.isBrick(1, 1));
        field2DPath.unsetBrick(1, 1);
        assertFalse(field2DPath.isBrick(1, 1));
    }

    @Test //bad idea for IntelliJ warnings (unused) suppress
    void suppress() {
        field2DPath.setShowNumbers(true);
        assertTrue(field2DPath.isShowNumbers());

        field2DPath.unsetBrick(1, 1).calculate();
    }
}
