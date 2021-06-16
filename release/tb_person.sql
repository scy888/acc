create table if not exists acc_batch.tb_person(
     id           varchar(50) primary key,
     name         varchar(50) null comment '用户名',
     address      varchar(50) null comment '地址',
     age          int         null comment '年龄',
     batch_date   date        null comment '生日',
     status       varchar(50) null comment '状态'
) comment '用户表', engine=innodb;
create unique index tb_person_name_index on tb_person(name);
alter table tb_person add index tb_person_status (status);
drop index tb_person_name_index on tb_person;
show index from tb_person;

