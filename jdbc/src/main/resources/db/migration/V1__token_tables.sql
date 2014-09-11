create table BS_TOKEN (
    ID int not null,
    FLOW_INSTANCE_ID int not null,
    CURRENT_NODE varchar(100) not null,
    SOURCE_NODE varchar(100) not null
);

create table BS_TOKEN_HISTORY (
    ID int not null,
    FLOW_INSTANCE_ID int not null,
    LAST_NODE varchar(100) not null,
    SOURCE_NODE varchar(100) not null
);