# practice-spring-batch
🧨 학습목적 - 스프링 배치

Spring Batch 를 사용해서 배치처리 해보기

## 학습 내용

- Spring-Batch 환경 구성
- 도메인 이해 – JobInstance, JobExecution, StepExecution, ExecutionContext, JobParameter, JobRepository, JobLauncher
- Job 구성 및 API 활용 - Job, Step, Flow, Tasklet
- Chunk 프로세스 – Chunk, ItemReader, ItemProcessor, ItemWriter
- 반복 및 내결함성 – Repeat, Skip, Retry, Listener
- 이벤트 리스너 – JobExecutionListener, StepListener, RetriyListner, SkipListener
- 멀티 스레드 배치 처리 - MultiThread Batch Process
- 테스트 및 운영 - TDD & JobExeplorer, JobRegistry, JobOperator

## 스프링 배치 5 Migration

- [Spring Batch 5.0 Migration Guide](https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide)
- Execution context serialization Updates
  - BATCH_JOB_EXECUTION_CONTEXT, BATCH_STEP_EXECUTION_CONTEXT 에 JSON이 아닌 BASE64 인코딩된 값이 insert된다.
  - base64가 아니라 json으로 저장하고 싶으면 practice.batch.ExecutionContextSerializerConfig를 참고한다.
- [@EnableBatchProcessing 을 쓰게되면 BatchAutoConfiguration이 적용되지 않는다.](https://umbum.dev/1320/)
- BuilderFactory대신 JobBuilder, StepBuilder 직접 사용
