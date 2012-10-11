package example;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class NumberUtilsNextOddTest {

	private NumberUtils numberUils;

	@Before
	public void InstantiateAnNumberUtilsBeforeEachTest() {
		numberUils = new NumberUtils();
	}

	@Test
	public void AnNextOddNumberOfAnOddNumberShouldBeThatNumberPlusTwo() {
		Assert.assertEquals(3, numberUils.nextOdd(1));
		Assert.assertEquals(5, numberUils.nextOdd(3));
	}

	@Test
	public void AnNextOddNumberOfAnEvenNumberShouldBeThatNumberPlusOne() {
		Assert.assertEquals(3, numberUils.nextOdd(2));
		Assert.assertEquals(5, numberUils.nextOdd(4));
	}

}
