package com.elliot.footballmanager.fixture;

import com.elliot.footballmanager.entity.FootballTeam;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RoundRobinFixtureGeneratorTest extends TestCase {

	@Test
	public void testReverseArrayFunction() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = RoundRobinFixtureGenerator.class.getDeclaredMethod("reverseArrayOrder", FootballTeam.class.arrayType());
		method.setAccessible(true);
		FootballTeam footballTeam1 = Mockito.mock(FootballTeam.class);
		FootballTeam footballTeam2 = Mockito.mock(FootballTeam.class);
		FootballTeam[] footballTeams = new FootballTeam[]{footballTeam1, footballTeam2};
		method.invoke(new RoundRobinFixtureGenerator(), (Object) footballTeams);
		assertEquals(footballTeams[0], footballTeam2);
	}

}