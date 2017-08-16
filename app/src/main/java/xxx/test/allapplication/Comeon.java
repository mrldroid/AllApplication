package xxx.test.allapplication;

/**
 * Created by neo on 17/1/5.
 */

public class Comeon {
    public static void main(String args[]){
        System.out.println(maxSubsequence(new int[]{-2,11,-4,13,-5,-2}));
    }
    private static int maxSubsequence(int a[]){
        int max = 0;
        for(int i = 0; i < a.length; i++){
            for(int j = i;j < a.length; j++){
                int subsequence = 0;
                for(int k = i; k <= j; k++){
                    subsequence += a[k];
                }
                if(max < subsequence){
                    max = subsequence;
                }
            }
        }
        return max;
    }
}
