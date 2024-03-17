package com.alphadominche.steampunkhmi.test;

import android.test.AndroidTestCase;

import com.alphadominche.steampunkhmi.SPRecipe;
import com.alphadominche.steampunkhmi.SPRecipeAgSliderInfo;
import com.alphadominche.steampunkhmi.SPRecipeDefaults;
import com.alphadominche.steampunkhmi.SPUser;

public class AgSliderInfoTest extends AndroidTestCase {
	SPRecipe mTeaRecipe;
	SPRecipe mCoffeeRecipe;
	@Override
	protected void setUp() throws Exception {
		mTeaRecipe = SPRecipeDefaults.getNewTeaRecipe(new SPUser(0, "user", SPUser.ADMIN));
		mTeaRecipe.setAgitation(0, 0, 5, 0, 0.5);
		mTeaRecipe.setAgitation(0, 1, 5, 10, 0.5);
		mTeaRecipe.setAgitation(0, 2, 5, 20, 0.5);
		mTeaRecipe.addStack();
		mTeaRecipe.removeAgitation(1, 2);
		mTeaRecipe.setAgitation(1, 0, 10, 0, 0.5);
		mTeaRecipe.setAgitation(1, 1, 10, 22, 0.5);
		mTeaRecipe.addStack();
		mTeaRecipe.removeAgitation(2, 2);
		mTeaRecipe.removeAgitation(2, 1);
		mTeaRecipe.setAgitation(0, 0, 13.5, 0, 0.25);
		
		mCoffeeRecipe = SPRecipeDefaults.getNewCoffeeRecipe(new SPUser(0, "user", SPUser.ADMIN));
		mCoffeeRecipe.setAgitation(0, 1, 1.5, 25, 0.75);
		super.setUp();
	}
	
	public void testGetStartTimeMin() {
		SPRecipeAgSliderInfo info = new SPRecipeAgSliderInfo(mTeaRecipe, 0, 0);
		assertEquals(info.getStartTimeMin(), 0);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 0, 1);
		assertEquals(info.getStartTimeMin(), 5);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 0, 2);
		assertEquals(info.getStartTimeMin(), 15);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 1, 0);
		assertEquals(info.getStartTimeMin(), 0);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 1, 1);
		assertEquals(info.getStartTimeMin(), 10);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 2, 0);
		assertEquals(info.getStartTimeMin(), 0);
		
		mTeaRecipe.setAgitation(2, 1, 2, 15, 0.3);
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 2, 1);
		assertEquals(info.getStartTimeMin(), 14);
		
		mTeaRecipe.removeAgitation(2, 1);
		
		info = new SPRecipeAgSliderInfo(mCoffeeRecipe, 0, 0);
		assertEquals(info.getStartTimeMin(), 0);
		
		info = new SPRecipeAgSliderInfo(mCoffeeRecipe, 0, 1);
		assertEquals(info.getStartTimeMin(), 3);
		
		info = new SPRecipeAgSliderInfo(mCoffeeRecipe, 0, 2);
		assertEquals(info.getStartTimeMin(), 27);
	}
	
	public void testGetStartTimeMax() {
		SPRecipeAgSliderInfo info = new SPRecipeAgSliderInfo(mTeaRecipe, 0, 0);
		assertEquals(info.getStartTimeMax(), 0);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 0, 1);
		assertEquals(info.getStartTimeMax(), 15);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 0, 2);
		assertEquals(info.getStartTimeMax(), 70);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 1, 0);
		assertEquals(info.getStartTimeMax(), 0);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 1, 1);
		assertEquals(info.getStartTimeMax(), 70);
		
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 2, 0);
		assertEquals(info.getStartTimeMax(), 0);
		
		mTeaRecipe.setAgitation(2, 1, 2, 15, 0.3);
		info = new SPRecipeAgSliderInfo(mTeaRecipe, 2, 1);
		assertEquals(info.getStartTimeMax(), 73);
		
		mTeaRecipe.removeAgitation(2, 1);
		
		info = new SPRecipeAgSliderInfo(mCoffeeRecipe, 0, 0);
		assertEquals(info.getStartTimeMax(), 0);
		
		info = new SPRecipeAgSliderInfo(mCoffeeRecipe, 0, 1);
		assertEquals(info.getStartTimeMax(), 48);
		
		info = new SPRecipeAgSliderInfo(mCoffeeRecipe, 0, 2);
		assertEquals(info.getStartTimeMax(), 73);
	}
	
	public void testGetStartTimeProgressMax() {
		
	}
	
	public void testGetStartTimeProgress() {
		
	}
	
	public void testGetStartTime() {
		
	}
	
	public void testGetDurationMax() {
		
	}
	
	public void testGetDurationProgress() {
		
	}
	
	public void testGetDuration() {
		
	}
	
	public void testGetPulseWidthMax() {
		
	}
	
	public void testGetPulseWidthProgress() {
		
	}
	
	public void testGetPulseWidth() {
		
	}
}
