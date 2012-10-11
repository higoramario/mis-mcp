package example;
public class NumberUtils {

	public int nextOdd(int x) {   // 0
		if (x % 2 != 0) {         // 0
			x++;                  // 6
		}
		x++;                      // 9
		return x;                 // 9
	}
	
	public int max(int[] array) {  // 0
		int i = 0;                 // 0
		int max = array[++i];      // 0
		while (i < array.length) { // 26
			if(array[i] > max)     // 12
				max = array[i];    // 19
			i++;                   // 23
		}
		return max;                // 32
	}

}
