create table FLOW_INSTANCE (
    ID varchar(40) not null primary key,
    DEFINITION_ID varchar(40) not null,
    CREATED bigint not null,
    VERSION bigint not null
);

create index IDX_INSTANCE_DEFINITION on FLOW_INSTANCE(DEFINITION_ID);

create table FLOW_TOKEN (
    ID varchar(40) not null primary key,
    CREATED bigint not null,
    VERSION bigint not null,
    FLOW_INSTANCE_ID varchar(40) not null,
    CURRENT_NODE varchar(100) not null,
    SOURCE_NODE varchar(100),
    IS_DEAD int default 0
);

create index IDX_TOKEN_INSTANCE on FLOW_TOKEN(FLOW_INSTANCE_ID);
create index IDX_TOKEN_NODE on FLOW_TOKEN(CURRENT_NODE);
create index IDX_TOKEN_SOURCE_NODE on FLOW_TOKEN(SOURCE_NODE);

alter table FLOW_TOKEN
add foreign key (FLOW_INSTANCE_ID)
references FLOW_INSTANCE(ID);
