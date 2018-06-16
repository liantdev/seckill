--���ݿ�ű�

--1.�������ݿ�
CREATE DATABASE seckill;

--2.������ɱ����
CREATE TABLE seckill(
seckillId bigint not null AUTO_INCREMENT COMMENT '��Ʒ���ID',
name varchar(100) not null COMMENT '��Ʒ����',
number int not null COMMENT '�������',
start_time datetime not null COMMENT '��ʼʱ��',
end_time datetime not null COMMENT '����ʱ��',
create_time timestamp not null default CURRENT_TIMESTAMP COMMENT '����ʱ��',
primary key(seckillId),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 default charset=utf8 COMMENT='��ɱ����';

--3.��ʼ����ɱ����
insert into 
	seckill(name, number, start_time, end_time)
values
	('iPhoneX', 120, '2018-05-01 00:00:00', '2018-05-03 23:59:59'),
	('С��MIX2', 500, '2018-05-01 00:00:00', '2018-05-03 23:59:59'),
	('OppoR15', 200, '2018-05-01 00:00:00', '2018-05-03 23:59:59'),
	('HUAWEIp20',300, '2018-05-01 00:00:00', '2018-05-03 23:59:59');

--4.��ɱ�ɹ���ϸ��
CREATE TABLE success_killed(
seckill_id bigint not null COMMENT '��ɱ��ƷId',
user_phone bigint not null COMMENT '�û��ֻ���',
state tinyint not null default -1 COMMENT '״̬��ʶ:-1 ��Ч 0���ɹ� 1���Ѹ���',
create_time timestamp not null COMMENT '����ʱ��',
primary key(seckill_id, user_phone),
key idx_create_time(create_time)
)ENGINE=InnoDB default charset=utf8 comment='��ɱ�ɹ���ϸ��';