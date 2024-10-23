package edu.sdccd.cisc191.template;



import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayTest {
//   expand, shrink
    ArrayOperations testInstance = ArrayOperations.getInstance();

    @Test
    public void testGetAtIndex(){
        ArrayOperations testInstance = new ArrayOperations();
        assertEquals(testInstance.getAtIndex(0),0);
    }

    @Test
    public void testSetAtIndex(){
        ArrayOperations testInstance = new ArrayOperations();
        String result = Arrays.toString(testInstance.printAll());
//        System.out.println(result);
        testInstance.setAtIndex(9,11);

        assertEquals(testInstance.getAtIndex(9),11);
    }

    @Test
    public void testPrintAll(){
        ArrayOperations testInstance = new ArrayOperations();
        String testPrintResult = Arrays.toString(testInstance.printAll());
        assertEquals(testPrintResult ,"[0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
    }

    @Test
    public void testFindIndexOf(){
        ArrayOperations testInstance = new ArrayOperations();
        testInstance.setAtIndex(9,11);
        assertEquals(testInstance.findIndexOf(0),0);

    }

    @Test
    public void deleteAtIndex(){
        ArrayOperations testInstance = ArrayOperations.getInstance();
        testInstance.setAtIndex(5,11);
        testInstance.deleteAtIndex(0);
        assertFalse(testInstance.getAtIndex(9) == 11);
    }

    @Test
    public void testExpandArray(){
        ArrayOperations testInstance = new ArrayOperations();
        testInstance.expandArray();
        assertEquals(testInstance.printAll().length,20);
    }

    @Test
    public void testShrinkArray(){
        ArrayOperations testInstance = new ArrayOperations();
        testInstance.shrinkArray();
        assertEquals(testInstance.printAll().length,5);
    }
}
