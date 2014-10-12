create table ASYNC_TASK (
    ID varchar(40) not null primary key,
    TASK_NODE_ID varchar(40) not null,
    INSTANCE_ID varchar(40) not null,
    DEFINITION_ID varchar(40) not null,
    CREATED bigint not null,
    DUE_DATE bigint not null,
    RETRIES bigint not null,
    MAX_RETRIES bigint not null,
    VERSION bigint not null
);

create table ASYNC_TASK_ERROR_DETAILS (
    ID varchar(40) not null primary key,
    ASYNC_TASK_ID varchar(40) not null,
    STACK_TRACE blob,
    EXCEPTION_TYPE varchar (1024) not null,
    MESSAGE varchar(2048),
    CREATED bigint not null,
    VERSION bigint not null
);
