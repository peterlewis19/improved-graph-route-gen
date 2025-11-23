import java.util.ArrayList;

public class ArrayListHelp {
    public ArrayListHelp(){}

    public static ArrayList<ArrayList<Integer>> sliceArrayListInteger(int startIndex, int endIndex, ArrayList<ArrayList<Integer>> arrayListToChop){
        ArrayList<ArrayList<Integer>> slicedArrayList = new ArrayList<>();

        for (int i=startIndex; i < endIndex; i++){
            slicedArrayList.add(arrayListToChop.get(i));
        }

        return slicedArrayList;
    }

    public static ArrayList<Integer> sliceArrayList(int startIndex, int endIndex, ArrayList<Integer> arrayListToChop){
        ArrayList<Integer> slicedArrayList = new ArrayList<>();

        for (int i=startIndex; i < endIndex; i++){
            slicedArrayList.add(arrayListToChop.get(i));
        }

        return slicedArrayList;
    }
}
