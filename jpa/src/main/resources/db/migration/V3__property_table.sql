create table INSTANCE_PROPERTY (
    ID varchar(40) not null primary key,
    CREATED bigint not null,
    VERSION bigint not null,
    INSTANCE_ID varchar(40) not null,
    VALUE_TYPE varchar(1024) not null,
    PROPERTY_KEY varchar(255) not null,
    STRING_VALUE varchar(4000),
    LONG_VALUE bigint,
    DOUBLE_VALUE NUMERIC(20,10),
    BYTE_ARRAY_VALUE blob
);

create index IDX_PROPERTY_INSTANCE on INSTANCE_PROPERTY(INSTANCE_ID);

alter table INSTANCE_PROPERTY
add foreign key (INSTANCE_ID)
references FLOW_INSTANCE(ID);