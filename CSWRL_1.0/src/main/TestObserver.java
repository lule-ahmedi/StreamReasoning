/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author Edi
 */
import java.util.Observable;
import java.util.Observer;
/*  ww w.  j  a v  a2  s  . c  om*/
class ObservedObject extends Observable {
   private String watchedValue;
   
   public ObservedObject(String value) {
      watchedValue = value;
   }
   
   public void setValue(String value) {
      if(!watchedValue.equals(value)) {
         System.out.println("Value changed to new value: "+value);
         watchedValue = value;

         setChanged();

         notifyObservers(value);
      }
   }
}
class MainObserver implements Observer {
   
   public void update(Observable obj, Object arg) {
      System.out.println("Update called with Arguments: "+arg);
   }
}

public class TestObserver{
   public static void main (String []args){

    int []list = {1,2,3,4,5};
    int number = 0;

    modify(number, list);

    System.out.println("number is: "+number);

    for (int i = 0; i < list.length; i++)
    {
        System.out.print(list[i]+" ");
    }

    System.out.println();
}
    public static void modify (int num, int []list){

        num = 3;
        list[0] = 3;
    }

}
