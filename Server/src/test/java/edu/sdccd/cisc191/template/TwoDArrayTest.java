package edu.sdccd.cisc191.template;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TwoDArrayTest {

    @Test
    public void testGetSetAtIndex(){
        TwoDArrayOperations testInstance = new TwoDArrayOperations(4,4);
        testInstance.setAtIndex(1,2,3);
        assertEquals(testInstance.getAtIndex(1,2),3);
    }

    @Test
    public void testPrintAll(){
        TwoDArrayOperations testInstance = new TwoDArrayOperations(4,4);
        String testPrintResult = testInstance.printAll();
        System.out.println(testPrintResult);
        assertEquals(testPrintResult ,"0 0 0 0 \n" + "0 0 0 0 \n" + "0 0 0 0 \n" + "0 0 0 0 \n");
    }

    @Test
    public void testFindIndexOf(){
        TwoDArrayOperations testInstance = new TwoDArrayOperations(4,4);
        testInstance.setAtIndex(1,2,3);
        assertEquals(testInstance.findIndexOf(3),"Value found at: [" + 1 + "][" + 2 + "]");

    }

    @Test
    public void deleteAtIndex(){
        TwoDArrayOperations testInstance = new TwoDArrayOperations(4,4);
        testInstance.setAtIndex(1,2,3);
        testInstance.deleteAtIndex(1,2);
        assertEquals(testInstance.getAtIndex(1,2), 0);
    }

    @Test
    public void testExpand(){
        TwoDArrayOperations testInstance = new TwoDArrayOperations(4,4);
        testInstance.expand(5,5);
        assertEquals(testInstance.printAll(),"0 0 0 0 0 \n" + "0 0 0 0 0 \n" + "0 0 0 0 0 \n" + "0 0 0 0 0 \n" + "0 0 0 0 0 \n");
    }

    @Test
    public void testShrinkArray(){
        TwoDArrayOperations testInstance = new TwoDArrayOperations(4,4);
        testInstance.shrink(1,1);
        assertEquals(testInstance.printAll(),"0 \n");
    }
}
