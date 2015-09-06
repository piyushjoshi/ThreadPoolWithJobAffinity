package apigee.assignment.threadpool;

/**
* Represents fixed size thread pool.
*/
public interface ThreadPoolWithJobAffinity {
	/**
	* Return the thread pool size
	*
	* @return an integer containing the poolSize.
	*/
	int poolSize();

	/**
	* Execute a given job. The jobId determines the thread
	* which executes the job.All the jobs submitted with
	* the same job Id should be executed by the same thread
	* of the pool. Multiple jobs submitted for a given job id
	* should be executed in the order of submission.
	*
	* @param jobId a string containing job id.
	* @param job a Runnable representing the job to be executed.
	*/
	void submit(String jobId, Runnable job);

	/**
	* Graceful shutdown of the thread pool.
	* Waits for all jobs to complete before shutdown.
	*/
	void shutdown();
}
