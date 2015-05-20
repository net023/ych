package com.ych.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class AdsNoiconAppReportJob implements Job {

//	private static final Logger LOG = Logger.getLogger(AdsNoiconAppReportJob.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println(11111);

	}
}
