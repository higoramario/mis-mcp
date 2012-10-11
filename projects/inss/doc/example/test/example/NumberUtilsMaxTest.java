package example;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class NumberUtilsMaxTest {

	private NumberUtils numberUils;

	@Before
	public void InstantiateAnNumberUtilsBeforeEachTest() {
		numberUils = new NumberUtils();
	}

	@Test
	public void AnMaxElementOfAnOrderedArraysShouldBeTheLastElement() {
		int[] orderedArray = new int[] { 1, 2, 3, 4, 5 };
		Assert.assertEquals(5, numberUils.max(orderedArray));
	}

	@Test
	public void AnMaxElementOfAnInverseOrderedArrayShouldBeTheFirstElement() {
		int[] inverseOrderedArray = new int[] { 5, 4, 3, 2, 1 };
		Assert.assertEquals(5, numberUils.max(inverseOrderedArray));
	}

	@Test
	public void ShouldReturnTheMaxValueCorrectlyIfTheMaxValueIsInTheMiddleOfAnArray() {
		int[] array = new int[] { 1, 2, 5, 4, 3 };
		Assert.assertEquals(5, numberUils.max(array));
	}

	@Test
	public void ShouldReturnTheMaxValueCorrectlyIfTheMaxValueIsTheFirstElementOfAnArray() {
		int[] array = new int[] { 5, 2, 1, 4, 3 };
		Assert.assertEquals(5, numberUils.max(array));
	}

	@Test
	public void ShouldReturnTheMaxValueCorrectlyIfAnArrayIsSingleElement() {
		int[] array = new int[] { 5 };
		Assert.assertEquals(5, numberUils.max(array));
	}

}
