create table sys_retry_task (
task_id serial not null primary key,
identity_name varchar(50) not null COMMENT '任务的唯一标识',
params varchar(4000) COMMENT '参数',
status smallint not null COMMENT '状态。1: 处理中，2: 成功，3: 失败',
retry_count int not null default 0 COMMENT '重试次数',
remark varchar(1000) COMMENT '备注',
create_date timestamp not null,
edit_date timestamp);

create index idx_identityname_status ON sys_retry_task(identity_name asc,status asc);