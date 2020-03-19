create table SysRetryTask (
TaskId bigint not null identity,
IdentityName varchar(50) not null,
Params nvarchar(4000),
Status tinyint not null,
RetryCount int not null default 0,
Remark nvarchar(1000),
CreateDate datetime not null,
EditDate datetime);

create index IDX_IdentityName_Status ON SysRetryTask(IdentityName asc,Status asc);

execute sp_addextendedproperty 'MS_Description', '任务的唯一标识','user', 'dbo', 'table', 'SysRetryTask', 'column', 'IdentityName';
execute sp_addextendedproperty 'MS_Description', '参数','user', 'dbo', 'table', 'SysRetryTask', 'column', 'Params';
execute sp_addextendedproperty 'MS_Description', '状态。1: 处理中，2: 成功，3: 失败','user', 'dbo', 'table', 'SysRetryTask', 'column', 'Status';
execute sp_addextendedproperty 'MS_Description', '重试次数','user', 'dbo', 'table', 'SysRetryTask', 'column', 'RetryCount';
execute sp_addextendedproperty 'MS_Description', '备注','user', 'dbo', 'table', 'SysRetryTask', 'column', 'Remark';
