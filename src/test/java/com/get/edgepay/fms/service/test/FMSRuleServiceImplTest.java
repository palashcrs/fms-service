package com.get.edgepay.fms.service.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.get.edgepay.fms.service.impl.FmsRuleServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class FMSRuleServiceImplTest {

	@InjectMocks
	private FmsRuleServiceImpl fmsRuleServiceImpl;

	@Before
	public void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this); //not required if we use MockitoJUnitRunner
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateRule() {
	}
}
