insert into pop_abnormal_order (id, serial_number, abnormal_type, waybill_code, order_code, pop_sup_no, rsv1,
                                current_num, actual_num, confirm_num, operator_code, operator_name, create_site_code,
                                create_site_name, create_time, confirm_time, update_time, abnormal_state, memo, yn)
values (1217691424705740800, 'Mary', 1, 'Jax', 'Joe', 'Jim', 'Jone', 678, 557, 673, 737, 'Mary', 7, 'Joe', NOW(),
        '2020-01-16 14:13:53.841', NOW(), 1, 'Stone', 1);


insert into waybill_consumable_record(
    dms_id,
    dms_name,
    waybill_code,
    confirm_status,
    modify_status,
    receive_user_code,
    receive_user_erp,
    receive_user_name,
    confirm_user_name,
    confirm_user_erp,
    receive_time,
    confirm_time,
    create_time,
    is_delete,
    ts
)
values
(
    910,
    '马驹桥分拣中心',
    'JDVC03978826121',
    0,
    0,
    24756,
    'bjxings',
    '邢松',
    '邢松',
    'bjxings',
    now(),
    now(),
    now(),
    0,
    now(3)
);


insert into unload_car
(		unload_car_id,seal_car_code,vehicle_number,start_site_code,start_site_name,end_site_code,end_site_name,
		seal_time,seal_code,batch_code,railWay_platForm,waybill_num,package_num,unload_user_erp,unload_user_name,
		distribute_time,update_user_erp,update_user_name,status,operate_user_erp,operate_user_name,operate_time,create_time,update_time,yn)
values
(       1,'SC12345678','京A66666',910,'马驹桥分拣中心',364605,'通州分拣中心',
        '2020-06-29 12:12:12','SC12345678','121212-131313-123456789','月-ANWS',123,234,'bjxings','邢松',
        '2020-06-29 12:12:13','bjxings','邢松',2,'bjxings','邢松','2020-06-29 12:12:13','2020-06-29 12:12:13','2020-06-29 12:12:13',1);

insert into unload_car_distribute
(		unload_distribute_id,seal_car_code,unload_user_erp,unload_user_name,
		unload_user_type,create_time,update_time,yn)
values
(       1,'SC12345678','bjxings','邢松',1,'2020-07-02 11:12:13','2020-01-01 11:12:13',1);

INSERT INTO `box_limit_config` (`id`, `site_name`, `site_id`, `limit_num`, `operator_erp`, `operator_site_id`, `operator_site_name`, `operating_time`, `create_time`, `update_time`, `yn`) VALUES ('23', '测试站点', '1', '400', 'test123', '100', '操作人站点', '2020-09-07 15:04:41', '2020-09-07 15:04:44', '2020-09-07 15:04:46', '1');
