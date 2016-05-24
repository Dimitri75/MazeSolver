package list;

import java.util.LinkedList;

/**
 * Created by Dimitri on 01/11/2015.
 */
public class CircularQueue<T> extends LinkedList<T>{
    protected int fixedSize;

    public CircularQueue(){
        super();
        this.fixedSize = Integer.MAX_VALUE;
    }

    public CircularQueue(int size){
        super();
        this.fixedSize = size;
    }

    public int getFixedSize(){
        return fixedSize;
    }


    public T popFirstAndRepushAtTheEnd(){
        T o = removeFirst();
        addLast(o);
        return o;
    }

    public T addAndPopExcedent(T o){
        addLast(o);

        if (size() > fixedSize) {
            T tmpFirst = removeFirst();
            return tmpFirst;
        }
        return null;
    }
}