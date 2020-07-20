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
