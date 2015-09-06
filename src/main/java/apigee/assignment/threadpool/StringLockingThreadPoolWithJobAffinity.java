package apigee.assignment.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * First go through SimpleThreadPoolWithJobAffinity
 * 
 * Works similar to SimpleThreadPoolWithJobAffinity.
 * But it ensures jobs with same jobID are executed in order of submission,
 * by locking on the interned jobID string, which is guaranteed to return same object for same jobID.
 * 
 * DrawBacks:
 * if the same jobID String is used for locking anywhere else in program, it will cause performance bottlenecks.
 * Not a recommended approach.
 * 
 * @author jpiyush
 *
 */
public class StringLockingThreadPoolWithJobAffinity implements ThreadPoolWithJobAffinity {

	private final ConcurrentMap<String, Future<ExecutorService>> executorMap;

	private volatile boolean isShutdown = false;

	public StringLockingThreadPoolWithJobAffinity() {
		executorMap = new ConcurrentHashMap<>();
	}

	@Override
	public int poolSize() {
		return executorMap.keySet().size();
	}

	@Override
	public void submit(String jobId, Runnable job) {
		synchronized (jobId.intern()) {
			Future<ExecutorService> f = executorMap.get(jobId);
			if (f == null) {
				Callable<ExecutorService> executorCreationTask = new Callable<ExecutorService>() {
					@Override
					public ExecutorService call() throws Exception {
						return Executors.newSingleThreadExecutor();
					}
				};
				FutureTask<ExecutorService> ft = new FutureTask<ExecutorService>(executorCreationTask);
				f = executorMap.putIfAbsent(jobId, ft);
				if (f == null)
					f = ft;
				ft.run();
			}
			Executor executor = null;
			try {
				executor = f.get();
			} catch (Exception e) {
				e.printStackTrace();
				throw new TaskAbandonmentException(e.getMessage());
			}
			if (!isShutdown)
				executor.execute(job);
		}
	}

	@Override
	public void shutdown() {
		this.isShutdown = true;
		for (String jobID : executorMap.keySet()) {
			try {
				executorMap.get(jobID).get().shutdown();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}