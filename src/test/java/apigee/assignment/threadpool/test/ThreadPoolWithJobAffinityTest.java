package apigee.assignment.threadpool.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

import net.jodah.concurrentunit.Waiter;

import org.junit.Test;

import apigee.assignment.threadpool.SimpleThreadPoolWithJobAffinity;
import apigee.assignment.threadpool.StringLockingThreadPoolWithJobAffinity;
import apigee.assignment.threadpool.ThreadPoolWithJobAffinity;

public class ThreadPoolWithJobAffinityTest {

	@Test
	public void shouldExecuteInSequece() throws Exception{
		final Waiter waiter = new Waiter();
		
		ThreadPoolWithJobAffinity basicThreadPool = new SimpleThreadPoolWithJobAffinity();
		testThreadPool(basicThreadPool, waiter);
		
		ThreadPoolWithJobAffinity stringLockingThreadPool = new StringLockingThreadPoolWithJobAffinity();
		testThreadPool(stringLockingThreadPool, waiter);
	}

	private void testThreadPool(ThreadPoolWithJobAffinity threadPool, Waiter waiter) throws TimeoutException {
		final ConcurrentMap<String, List<String>> responses = new ConcurrentHashMap<>();
		responses.put("1", new ArrayList<String>());
		responses.put("2", new ArrayList<String>());
		responses.put("3", new ArrayList<String>());
		
		threadPool.submit("1", new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				responses.get("1").add("JobID=1, taskNumber=1");
				waiter.resume();
			}
		});
		threadPool.submit("1", new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				responses.get("1").add("JobID=1, taskNumber=2");
				waiter.resume();
			}
		});
		threadPool.submit("2", new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				responses.get("2").add("JobID=2, taskNumber=1");
				waiter.resume();
			}
		});
		
		threadPool.submit("1", new Runnable(){
			@Override
			public void run() {
				responses.get("1").add("JobID=1, taskNumber=3");
				waiter.resume();
			}
		});
		
		threadPool.submit("2", new Runnable(){
			@Override
			public void run() {
				responses.get("2").add("JobID=2, taskNumber=2");
				waiter.resume();
			}
		});
		
		waiter.await(30000, 5);
		
		System.out.println(responses);
		
		for(String jobID : responses.keySet()){
			List<String> list = responses.get(jobID);
			for(int i = 0; i < list.size(); i++){
				waiter.assertEquals(String.format("JobID=%s, taskNumber=%d", jobID, i+1), list.get(i));
			}
		}
	}
	
}
